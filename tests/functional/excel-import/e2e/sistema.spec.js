import { test, expect } from '@playwright/test'
import { Client } from 'pg'
import { randomUUID } from 'node:crypto'

async function entrar(page) {
  await page.goto('/#/login')
  await page.getByPlaceholder('E-mail').fill(process.env.STECAR_ADMIN_EMAIL)
  await page.getByPlaceholder('Senha').fill(process.env.STECAR_ADMIN_PASSWORD)
  await page.getByRole('button', { name: 'Entrar' }).click()
  await expect(page).toHaveURL(/dashboard/)
}

async function consultarBanco(sql, valores) {
  const client = new Client({
    host: process.env.STECAR_TEST_DB_HOST,
    port: Number(process.env.STECAR_TEST_DB_PORT),
    database: process.env.STECAR_TEST_DB_NAME,
    user: process.env.STECAR_TEST_DB_USER,
    password: process.env.STECAR_TEST_DB_PASSWORD,
  })
  await client.connect()
  try {
    return (await client.query(sql, valores)).rows
  } finally {
    await client.end()
  }
}

async function selecionarOpcao(page, seletor, nome) {
  await seletor.click()
  await expect(seletor).toHaveAttribute('aria-expanded', 'true')
  const menu = page.locator('.q-menu').last()
  await menu.evaluate(elemento => { elemento.scrollTop = elemento.scrollHeight })
  const opcao = page.getByRole('option', { name: nome, exact: true })
  await expect(opcao).toBeVisible()
  await opcao.click()
}

test('redireciona paginas protegidas para login sem autenticacao', async ({ page }) => {
  await page.goto('/#/bens')
  await expect(page).toHaveURL(/login/)
  await expect(page.getByPlaceholder('E-mail')).toBeVisible()
})

test('carrega as paginas funcionais e obtem dados das APIs', async ({ page }) => {
  await entrar(page)
  await page.goto('/#/perfil')
  const errosJavascript = []
  page.on('pageerror', erro => errosJavascript.push(erro.message))
  const paginas = [
    { rota: 'dashboard', endpoint: '/api/dashboard' },
    { rota: 'bens', endpoint: '/api/bens' },
    { rota: 'outros-itens', endpoint: '/api/outros-itens' },
    { rota: 'movimentacoes', endpoint: '/api/movimentacoes' },
    { rota: 'historicos', endpoint: '/api/movimentacoes' },
    { rota: 'consertos', endpoint: '/api/consertos' },
    { rota: 'pessoas', endpoint: '/api/pessoas' },
    { rota: 'departamentos', endpoint: '/api/departamentos' },
    { rota: 'escritorios', endpoint: '/api/escritorios' },
    { rota: 'usuarios', endpoint: '/api/usuarios' },
    { rota: 'perfil', endpoint: '/api/auth/me' },
  ]
  for (const { rota, endpoint } of paginas) {
    const respostaApi = page.waitForResponse(response => new URL(response.url()).pathname === endpoint)
    await page.goto(`/#/${rota}`)
    const response = await respostaApi.catch(error => {
      throw new Error(`A pagina ${rota} nao chamou ${endpoint}: ${error.message}`)
    })
    expect(response.status()).toBeGreaterThanOrEqual(200)
    expect(response.status()).toBeLessThan(300)
    await expect(page.locator('.q-page')).toBeVisible()
    await expect(page.locator('.q-banner.bg-negative')).toHaveCount(0)
  }
  expect(errosJavascript).toEqual([])
})

test('cadastra escritorio, departamento e pessoa pela interface e confere os vinculos', async ({ page }) => {
  await entrar(page)
  const id = randomUUID().slice(0, 8).toUpperCase()
  const escritorioNome = `TESTE_E2E_ESCRITORIO_${id}`
  const departamentoNome = `TESTE_E2E_DEPARTAMENTO_${id}`
  const pessoaNome = `TESTE_E2E_PESSOA_${id}`
  const email = `${id.toLowerCase()}@stecar.test`

  await page.goto('/#/escritorios')
  await page.getByRole('button', { name: /Novo Escrit/ }).click()
  await page.getByPlaceholder('Ex: Filial Feira de Santana').fill(escritorioNome)
  await page.getByPlaceholder('Ex: Feira de Santana').fill('Salvador')
  const criarEscritorio = page.waitForResponse(response =>
    response.request().method() === 'POST' && new URL(response.url()).pathname === '/api/escritorios')
  await page.getByRole('button', { name: 'Salvar', exact: true }).click()
  expect((await criarEscritorio).status()).toBe(201)
  await expect(page.getByText(escritorioNome, { exact: true })).toBeVisible()

  const carregarListas = Promise.all([
    page.waitForResponse(response =>
      response.request().method() === 'GET' && new URL(response.url()).pathname === '/api/escritorios'),
    page.waitForResponse(response =>
      response.request().method() === 'GET' && new URL(response.url()).pathname === '/api/departamentos'),
  ])
  await page.goto('/#/departamentos')
  const [respostaListaEscritorios, respostaListaDepartamentos] = await carregarListas
  expect(respostaListaEscritorios.status()).toBe(200)
  expect(respostaListaDepartamentos.status()).toBe(200)
  const listaEscritorios = await respostaListaEscritorios.json()
  expect(listaEscritorios.some(item => item.nome === escritorioNome)).toBe(true)
  await page.getByRole('button', { name: 'Novo Departamento' }).click()
  await page.getByPlaceholder('Digite o nome do departamento').fill(departamentoNome)
  const seletorEscritorio = page.getByRole('combobox', { name: 'Selecione o escritório' })
  await selecionarOpcao(page, seletorEscritorio, escritorioNome)
  const criarDepartamento = page.waitForResponse(response =>
    response.request().method() === 'POST' && new URL(response.url()).pathname === '/api/departamentos')
  await page.getByRole('button', { name: 'Cadastrar Departamento' }).click()
  expect((await criarDepartamento).status()).toBe(201)
  await expect(page.getByText(departamentoNome, { exact: true })).toBeVisible()

  await page.goto('/#/pessoas')
  await page.getByRole('button', { name: /Nova Pessoa/ }).first().click()
  const formulario = page.getByRole('dialog')
  await formulario.locator('input').nth(0).fill(pessoaNome)
  await formulario.locator('input').nth(1).fill(String(Date.now()).slice(-11))
  await formulario.locator('input').nth(2).fill(email)
  await formulario.locator('input').nth(4).fill('Analista de automacao')
  await selecionarOpcao(page, formulario.locator('.q-select__focus-target').nth(1), escritorioNome)
  await selecionarOpcao(page, formulario.locator('.q-select__focus-target').nth(2), departamentoNome)
  const criarPessoa = page.waitForResponse(response =>
    response.request().method() === 'POST' && new URL(response.url()).pathname === '/api/pessoas')
  await page.getByRole('button', { name: 'Salvar Registro' }).click()
  expect((await criarPessoa).status()).toBe(201)
  await expect(page.getByText(pessoaNome, { exact: true })).toBeVisible()

  const dados = await consultarBanco(`
    SELECT e.nome AS escritorio, d.nome AS departamento, d.escritorio_id,
           p.nome_completo AS pessoa, p.email, p.escritorio_id AS pessoa_escritorio_id
    FROM pessoa p JOIN escritorio e ON e.id = p.escritorio_id
    JOIN departamento d ON d.id = p.departamento_id WHERE p.email = $1
  `, [email])
  expect(dados).toHaveLength(1)
  expect(dados[0]).toMatchObject({ escritorio: escritorioNome, departamento: departamentoNome, pessoa: pessoaNome, email })
  expect(dados[0].pessoa_escritorio_id).toBe(dados[0].escritorio_id)
})
