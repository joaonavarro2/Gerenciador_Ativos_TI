<template>
  <div class="login-page">
    <div class="login-decoration login-decoration-top" />
    <div class="login-decoration login-decoration-bottom" />

    <div class="login-card">
      <div class="login-brand">
        <div class="login-brand-icon">
          <q-icon name="eco" size="30px" />
        </div>
        <div class="login-brand-name">
          <strong>STECAR</strong>
        </div>
      </div>

      <q-form class="login-form" @submit.prevent="entrar">
        <q-input
          v-model="form.email"
          class="login-field"
          outlined
          dense
          type="email"
          placeholder="E-mail"
          autocomplete="username"
          :rules="[valorObrigatorio, emailValido]"
          :error="!!emailError"
          :error-message="emailError"
          hide-bottom-space
        >
          <template #prepend><q-icon name="mail_outline" /></template>
        </q-input>

        <q-input
          v-model="form.senha"
          class="login-field"
          outlined
          dense
          :type="mostrarSenha ? 'text' : 'password'"
          placeholder="Senha"
          autocomplete="current-password"
          :rules="[valorObrigatorio]"
          :error="!!senhaError"
          :error-message="senhaError"
          hide-bottom-space
        >
          <template #prepend><q-icon name="lock_outline" /></template>
          <template #append>
            <q-btn
              flat
              round
              dense
              type="button"
              :icon="mostrarSenha ? 'visibility_off' : 'visibility'"
              class="login-password-toggle"
              :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'"
              @click="mostrarSenha = !mostrarSenha"
            />
          </template>
        </q-input>

        <div class="login-options">
          <q-checkbox v-model="lembrarMe" dense label="Lembrar-me" class="login-remember" />
          <q-btn
            flat
            dense
            no-caps
            type="button"
            label="Esqueci minha senha"
            class="login-link"
            @click="recuperarSenha"
          />
        </div>

        <q-btn
          unelevated
          no-caps
          color="positive"
          label="Entrar"
          type="submit"
          :loading="enviando"
          class="login-submit"
        />

        <div class="login-register">
          <span>Não possui uma conta?</span>
          <q-btn flat dense no-caps type="button" label="Cadastre-se" class="login-link" @click="cadastrar" />
        </div>
      </q-form>

      <div class="login-footer">Sistema de Gestão Agrorrural - Governo da Bahia</div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import { api } from '@/boot/axios.js'
import { salvarSessao } from '@/services/auth.js'

const router = useRouter()
const $q = useQuasar()
const lembrarMe = ref(false)
const mostrarSenha = ref(false)
const emailError = ref('')
const senhaError = ref('')
const form = reactive({ email: '', senha: '' })
const enviando = ref(false)

function limparErrosLogin() {
  emailError.value = ''
  senhaError.value = ''
}

onMounted(() => {
  if (router.currentRoute.value.query.cadastro !== 'sucesso') {
    return
  }

  $q.notify({
    type: 'positive',
    message: 'Usuário cadastrado com sucesso.',
    position: 'top',
    timeout: 4000,
  })

  router.replace({ path: '/login' })
})

function valorObrigatorio(valor) {
  return !!valor || 'Campo obrigatório'
}

function emailValido(valor) {
  return /.+@.+\..+/.test(valor) || 'Informe um e-mail válido'
}

async function entrar() {
  limparErrosLogin()

  if (!form.email) {
    emailError.value = 'Campo obrigatório'
    return
  }

  if (!/.+@.+\..+/.test(form.email)) {
    emailError.value = 'Informe um e-mail válido'
    return
  }

  if (!form.senha) {
    senhaError.value = 'Campo obrigatório'
    return
  }

  enviando.value = true
  try {
    const { data } = await api.post('/auth/login', {
      email: form.email,
      senha: form.senha,
    })
    salvarSessao(data, lembrarMe.value)
    await router.push(router.currentRoute.value.query.redirect || '/dashboard')
  } catch (error) {
    const status = error.response?.status
    const payload = error.response?.data
    const rawMensagem =
      payload?.mensagem ||
      payload?.message ||
      payload?.error ||
      payload?.detail ||
      error.message ||
      ''
    const texto = String(rawMensagem).toLowerCase()

    if (
      status === 404 ||
      texto.includes('usuário não cadastrado') ||
      texto.includes('user not found')
    ) {
      emailError.value = 'Usuário não cadastrado. Verifique o e-mail ou cadastre-se.'
      return
    }

    if (
      status === 401 ||
      status === 403 ||
      texto.includes('senha incorreta') ||
      texto.includes('bad credentials') ||
      texto.includes('credenciais')
    ) {
      senhaError.value = 'Senha incorreta. Verifique a senha e tente novamente.'
      return
    }

    $q.notify({
      type: 'warning',
      message: 'Não foi possível entrar no sistema. Tente novamente.',
      position: 'top',
      timeout: 5000,
    })
  } finally {
    enviando.value = false
  }
}

function recuperarSenha() {
  router.push('/recuperar-senha')
}

function cadastrar() {
  router.push('/cadastro-usuario')
}
</script>
