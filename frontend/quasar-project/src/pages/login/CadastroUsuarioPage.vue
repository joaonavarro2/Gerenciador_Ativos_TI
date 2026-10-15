<template>
  <div ref="authPage" class="auth-page">
    <div class="auth-decoration auth-decoration-top" />
    <div class="auth-decoration auth-decoration-bottom" />

    <div class="auth-card auth-card-register">
      <div class="auth-brand auth-brand-compact">
        <div class="auth-brand-icon"><q-icon name="eco" size="30px" /></div>
        <div class="auth-brand-name"><span>Data</span><strong>CAR</strong></div>
      </div>

      <div class="auth-heading">Novo usuário</div>
      <div class="auth-subheading">Preencha os dados do novo usuário do sistema.</div>

      <q-form class="auth-form" @submit.prevent="cadastrar">
        <q-input v-model="form.nome" class="auth-field" outlined dense placeholder="Nome completo" autocomplete="name" :rules="[obrigatorio]" hide-bottom-space>
          <template #prepend><q-icon name="person_outline" /></template>
        </q-input>
        <q-input v-model="form.email" class="auth-field" outlined dense type="email" placeholder="E-mail" autocomplete="email" :rules="[obrigatorio, emailValido]" hide-bottom-space>
          <template #prepend><q-icon name="mail_outline" /></template>
        </q-input>

        <div class="auth-field-row">
          <q-input v-model="form.senha" class="auth-field" outlined dense :type="mostrarSenha ? 'text' : 'password'" placeholder="Senha" autocomplete="new-password" :rules="[obrigatorio, senhaValida]" hide-bottom-space>
            <template #prepend><q-icon name="lock_outline" /></template>
            <template #append><q-btn flat round dense type="button" :icon="mostrarSenha ? 'visibility_off' : 'visibility'" class="auth-password-toggle" :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'" @click="mostrarSenha = !mostrarSenha" /></template>
          </q-input>
          <q-input v-model="form.confirmarSenha" class="auth-field" outlined dense :type="mostrarConfirmacao ? 'text' : 'password'" placeholder="Confirme a senha" autocomplete="new-password" :rules="[obrigatorio, senhasIguais]" hide-bottom-space>
            <template #prepend><q-icon name="lock_outline" /></template>
            <template #append><q-btn flat round dense type="button" :icon="mostrarConfirmacao ? 'visibility_off' : 'visibility'" class="auth-password-toggle" :aria-label="mostrarConfirmacao ? 'Ocultar senha' : 'Mostrar senha'" @click="mostrarConfirmacao = !mostrarConfirmacao" /></template>
          </q-input>
        </div>

        <div class="auth-field-hint"><q-icon name="info_outline" /> Novas contas são criadas como usuário padrão.</div>

        <q-btn unelevated no-caps icon="person_add" label="Cadastrar Usuário" type="submit" :loading="enviando" class="auth-submit auth-submit-register" />
      </q-form>

      <q-btn flat no-caps icon="west" label="Voltar para o login" class="auth-back" @click="voltar" />
      <div class="auth-footer">Sistema de Gestão Agrorrural - Governo da Bahia</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import { api } from '@/boot/axios.js'

const $q = useQuasar()
const router = useRouter()
const mostrarSenha = ref(false)
const mostrarConfirmacao = ref(false)
const enviando = ref(false)
const form = ref({ nome: '', email: '', senha: '', confirmarSenha: '' })

function obrigatorio(valor) {
  return !!valor || 'Campo obrigatório. Preencha este campo para continuar.'
}

function emailValido(valor) {
  return /.+@.+\..+/.test(valor) || 'Informe um e-mail válido para prosseguir.'
}

function senhaValida(valor) {
  return (
    (valor && valor.length >= 8 && valor.length <= 100) ||
    'A senha deve ter entre 8 e 100 caracteres para maior segurança.'
  )
}

function senhasIguais(valor) {
  return valor === form.value.senha || 'As senhas precisam ser iguais para confirmar o cadastro.'
}

function exibirErroFormulario(mensagem) {
  $q.notify({
    type: 'warning',
    message: mensagem,
    position: 'top',
    timeout: 5000,
    actions: [{ label: 'OK', color: 'white' }],
  })
}

async function cadastrar() {
  if (!form.value.nome || !form.value.email || !form.value.senha || !form.value.confirmarSenha) {
    exibirErroFormulario('Preencha todos os campos antes de concluir o cadastro.')
    return
  }

  if (!/.+@.+\..+/.test(form.value.email)) {
    exibirErroFormulario('Informe um e-mail válido para continuar o cadastro.')
    return
  }

  if (form.value.senha.length < 8 || form.value.senha.length > 100) {
    exibirErroFormulario('A senha deve ter entre 8 e 100 caracteres para maior segurança.')
    return
  }

  if (form.value.senha !== form.value.confirmarSenha) {
    exibirErroFormulario('As senhas precisam ser iguais para confirmar o cadastro.')
    return
  }

  enviando.value = true
  try {
    await api.post('/auth/cadastro', {
      nomeCompleto: form.value.nome,
      email: form.value.email,
      senha: form.value.senha,
    })
    await router.replace({ path: '/login', query: { cadastro: 'sucesso' } })
  } catch (error) {
    const status = error.response?.status
    const payload = error.response?.data
    const mensagem =
      status === 409 || /email|e-mail/i.test(String(payload?.mensagem || payload?.detail || ''))
        ? 'Este e-mail já está cadastrado. Tente outro endereço ou faça login.'
        : payload?.mensagem || payload?.detail || Object.values(payload || {})[0] || 'Não foi possível realizar o cadastro.'

    $q.notify({
      type: 'negative',
      message: mensagem,
      position: 'top',
      timeout: 5000,
      actions: [{ label: 'OK', color: 'white' }],
    })
  } finally {
    enviando.value = false
  }
}
function voltar() { router.push('/login') }
</script>
