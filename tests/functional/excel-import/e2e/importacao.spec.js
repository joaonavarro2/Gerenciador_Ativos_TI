import { test, expect } from '@playwright/test'
import { Client } from 'pg'
import { randomUUID } from 'node:crypto'
import { deflateRawSync } from 'node:zlib'

const prefix = `TESTE_EXCEL_E2E_${randomUUID().slice(0, 8).toUpperCase()}`
const appUrl = (path) => new URL(path, process.env.APP_BASE_URL).toString()

function criarPlanilha(linhas) {
  const tabelaCrc = Array.from({ length: 256 }, (_, n) => {
    let c = n
    for (let k = 0; k < 8; k++) c = (c & 1) ? (0xedb88320 ^ (c >>> 1)) : (c >>> 1)
    return c >>> 0
  })
  const crc32 = (buffer) => {
    let crc = 0xffffffff
    for (const byte of buffer) crc = tabelaCrc[(crc ^ byte) & 0xff] ^ (crc >>> 8)
    return (crc ^ 0xffffffff) >>> 0
  }
  const xml = (value) => String(value ?? '').replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&apos;')
  const columnName = (index) => {
    let name = ''
    for (let n = index + 1; n; n = Math.floor((n - 1) / 26)) name = String.fromCharCode(65 + ((n - 1) % 26)) + name
    return name
  }
  const values = [
    ['Código', 'Patrimônio', 'Instituição', 'Nome', 'Categoria', 'Número de Série', 'Fabricante', 'Modelo', 'Descrição', 'Status', 'Data de Aquisição', 'Escritório', 'Departamento', 'Responsável'],
    ...linhas,
  ]
  const sheetRows = values.map((row, rowIndex) => `<row r="${rowIndex + 1}">${row.map((value, colIndex) => {
    const ref = `${columnName(colIndex)}${rowIndex + 1}`
    return `<c r="${ref}" t="inlineStr"><is><t xml:space="preserve">${xml(value)}</t></is></c>`
  }).join('')}</row>`).join('')
  const files = [
    ['[Content_Types].xml', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/><Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/></Types>'],
    ['_rels/.rels', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/></Relationships>'],
    ['xl/workbook.xml', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><sheets><sheet name="Importação" sheetId="1" r:id="rId1"/></sheets></workbook>'],
    ['xl/_rels/workbook.xml.rels', '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/></Relationships>'],
    ['xl/worksheets/sheet1.xml', `<?xml version="1.0" encoding="UTF-8" standalone="yes"?><worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>${sheetRows}</sheetData></worksheet>`],
  ]
  const localParts = []
  const centralParts = []
  let offset = 0
  for (const [path, content] of files) {
    const name = Buffer.from(path)
    const data = Buffer.from(content)
    const compressed = deflateRawSync(data)
    const checksum = crc32(data)
    const local = Buffer.alloc(30 + name.length)
    local.writeUInt32LE(0x04034b50, 0); local.writeUInt16LE(20, 4); local.writeUInt16LE(8, 8)
    local.writeUInt32LE(checksum, 14); local.writeUInt32LE(compressed.length, 18); local.writeUInt32LE(data.length, 22)
    local.writeUInt16LE(name.length, 26); name.copy(local, 30)
    localParts.push(local, compressed)
    const central = Buffer.alloc(46 + name.length)
    central.writeUInt32LE(0x02014b50, 0); central.writeUInt16LE(20, 4); central.writeUInt16LE(20, 6)
    central.writeUInt16LE(8, 10); central.writeUInt32LE(checksum, 16)
    central.writeUInt32LE(compressed.length, 20); central.writeUInt32LE(data.length, 24)
    central.writeUInt16LE(name.length, 28); central.writeUInt32LE(offset, 42); name.copy(central, 46)
    centralParts.push(central)
    offset += local.length + compressed.length
  }
  const centralDirectory = Buffer.concat(centralParts)
  const end = Buffer.alloc(22)
  end.writeUInt32LE(0x06054b50, 0); end.writeUInt16LE(files.length, 8); end.writeUInt16LE(files.length, 10)
  end.writeUInt32LE(centralDirectory.length, 12); end.writeUInt32LE(offset, 16)
  return Buffer.concat([...localParts, centralDirectory, end])
}

async function autenticar(page) {
  await page.goto('/#/login')
  await page.getByPlaceholder('E-mail').fill(process.env.STECAR_ADMIN_EMAIL)
  await page.getByPlaceholder('Senha').fill(process.env.STECAR_ADMIN_PASSWORD)
  await page.getByRole('button', { name: 'Entrar' }).click()
  await expect(page).toHaveURL(/dashboard/)
  await page.goto('/#/bens')
  await expect(page.getByText('Inventário de Bens')).toBeVisible()
}

async function obterToken(page) {
  return page.evaluate(() => localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token'))
}

async function obterOpcoes(page, token) {
  const response = await page.request.get(appUrl('/api/bens/opcoes'), {
    headers: { Authorization: `Bearer ${token}` },
  })
  expect(response.ok()).toBeTruthy()
  const opcoes = await response.json()
  expect(opcoes.escritorios.length).toBeGreaterThan(0)
  expect(opcoes.departamentos.length).toBeGreaterThan(0)
  return { escritorio: opcoes.escritorios[0].nome, departamento: opcoes.departamentos[0].nome }
}

async function contarBanco(codigo) {
  const client = new Client({
    host: process.env.STECAR_TEST_DB_HOST,
    port: Number(process.env.STECAR_TEST_DB_PORT),
    database: process.env.STECAR_TEST_DB_NAME,
    user: process.env.STECAR_TEST_DB_USER,
    password: process.env.STECAR_TEST_DB_PASSWORD,
  })
  await client.connect()
  try {
    const result = await client.query(`
      SELECT i.codigo, i.nome, i.patrimonio, i.serial, i.status, i.data_aquisicao,
             e.nome AS escritorio, d.nome AS departamento, p.id AS pessoa_id
      FROM item i
      JOIN escritorio e ON e.id = i.escritorio_id
      JOIN departamento d ON d.id = i.departamento_id
      LEFT JOIN pessoa p ON p.id = i.pessoa_id
      WHERE i.codigo = $1
    `, [codigo])
    return result.rows[0] || null
  } finally {
    await client.end()
  }
}

async function contarPrefixoBanco() {
  const client = new Client({
    host: process.env.STECAR_TEST_DB_HOST,
    port: Number(process.env.STECAR_TEST_DB_PORT),
    database: process.env.STECAR_TEST_DB_NAME,
    user: process.env.STECAR_TEST_DB_USER,
    password: process.env.STECAR_TEST_DB_PASSWORD,
  })
  await client.connect()
  try {
    const result = await client.query('SELECT COUNT(*)::int AS total FROM item WHERE codigo LIKE $1', [`${prefix}%`])
    return result.rows[0].total
  } finally {
    await client.end()
  }
}

test('importa XLSX pela interface, consulta pela API e confere PostgreSQL', async ({ page }) => {
  await autenticar(page)
  const token = await obterToken(page)
  const opcoes = await obterOpcoes(page, token)
  const codigo = `${prefix}_001`
  const patrimonio = `${prefix}_PAT_001`
  const nome = 'Notebook de automação São João – ação çãõ'

  await page.getByRole('button', { name: 'Importar Excel' }).click()
  await page.locator('input[type="file"]').setInputFiles({
    name: 'bens-teste.xlsx',
    mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    buffer: criarPlanilha([[
      codigo, patrimonio, 'CAR', nome, 'Notebook', 'SN-E2E-001', 'Fabricante E2E', 'Modelo E2E',
      'Descrição com acentuação', 'Ativo', '2024-02-29', opcoes.escritorio, opcoes.departamento, '',
    ]]),
  })
  await page.getByRole('button', { name: 'Validar planilha' }).click()
  await expect(page.getByText('Confirmar importação de 1 registro(s)')).toBeVisible()
  await page.getByRole('button', { name: 'Confirmar importação de 1 registro(s)' }).click()
  await expect(page.getByText('1 registro(s) importado(s); 0 rejeitado(s).')).toBeVisible()

  await expect.poll(async () => {
    const response = await page.request.get(appUrl(`/api/bens?busca=${encodeURIComponent(codigo)}`), {
      headers: { Authorization: `Bearer ${token}` },
    })
    if (!response.ok()) return null
    return (await response.json())[0]?.codigo || null
  }).toBe(codigo)
  const apiResponse = await page.request.get(appUrl(`/api/bens?busca=${encodeURIComponent(codigo)}`), {
    headers: { Authorization: `Bearer ${token}` },
  })
  const apiBem = (await apiResponse.json())[0]
  expect(apiBem.nome).toBe(nome)
  expect(apiBem.patrimonio).toBe(patrimonio)
  expect(apiBem.serial).toBe('SN-E2E-001')
  expect(apiBem.escritorio).toBe(opcoes.escritorio)
  expect(apiBem.departamento).toBe(opcoes.departamento)

  await expect(page.getByPlaceholder(/Buscar por ID/)).toBeVisible()
  await page.getByPlaceholder(/Buscar por ID/).fill(codigo)
  await expect(page.getByText(nome)).toBeVisible()

  const registroBanco = await contarBanco(codigo)
  expect(registroBanco).toMatchObject({
    codigo,
    nome,
    patrimonio,
    serial: 'SN-E2E-001',
    status: 'Ativo',
    escritorio: opcoes.escritorio,
    departamento: opcoes.departamento,
  })
  expect(registroBanco.data_aquisicao.toISOString()).toContain('2024-02-29')
})

test('rejeita arquivo com extensão PDF e não grava registros', async ({ page }) => {
  await autenticar(page)
  let requisicoesImportacao = 0
  page.on('request', (request) => {
    if (request.method() === 'POST' && request.url().includes('/api/importacao-excel/')) requisicoesImportacao++
  })
  await page.getByRole('button', { name: 'Importar Excel' }).click()
  await page.locator('input[type="file"]').setInputFiles({
    name: `${prefix}.pdf`,
    mimeType: 'application/pdf',
    buffer: Buffer.from('%PDF-1.7 teste inválido'),
  })
  const inputArquivo = page.locator('input[type="file"]')
  await expect(inputArquivo).toHaveAttribute('accept', '.xlsx')
  expect(await inputArquivo.evaluate(input => input.files?.length || 0)).toBe(0)
  await expect(page.getByRole('button', { name: 'Validar planilha' })).toHaveCount(0)
  expect(requisicoesImportacao).toBe(0)
  expect(await contarPrefixoBanco()).toBe(1)
})

test('informa erro para conteúdo corrompido com extensão XLSX e não grava registros', async ({ page }) => {
  await autenticar(page)
  let requisicoesImportacao = 0
  page.on('request', (request) => {
    if (request.method() === 'POST' && request.url().includes('/api/importacao-excel/')) requisicoesImportacao++
  })
  await page.getByRole('button', { name: 'Importar Excel' }).click()
  await page.locator('input[type="file"]').setInputFiles({
    name: `${prefix}-corrompido.xlsx`,
    mimeType: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    buffer: Buffer.from('%PDF-1.7 conteúdo inválido para XLSX'),
  })
  await expect(page.getByRole('alert')).toBeVisible()
  await expect(page.getByRole('button', { name: 'Validar planilha' })).toHaveCount(0)
  expect(requisicoesImportacao).toBe(0)
  expect(await contarPrefixoBanco()).toBe(1)
})
