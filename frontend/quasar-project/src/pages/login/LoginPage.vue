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
          <span>Data</span><strong>CAR</strong>
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
          hide-bottom-space
        >
          <template #prepend><q-icon name="lock_outline" /></template>
          <template #append>
            <q-btn
              flat
              round
              dense
              :icon="mostrarSenha ? 'visibility_off' : 'visibility'"
              class="login-password-toggle"
              :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'"
              @click="mostrarSenha = !mostrarSenha"
            />
          </template>
        </q-input>

        <div class="login-options">
          <q-checkbox v-model="lembrarMe" dense label="Lembrar-me" class="login-remember" />
          <q-btn flat dense no-caps label="Esqueci minha senha" class="login-link" @click="recuperarSenha" />
        </div>

        <q-btn
          unelevated
          no-caps
          color="positive"
          label="Entrar"
          type="submit"
          class="login-submit"
        />

        <div class="login-register">
          <span>Não possui uma conta?</span>
          <q-btn flat dense no-caps label="Cadastre-se" class="login-link" @click="cadastrar" />
        </div>
      </q-form>

      <div class="login-footer">Sistema de Gestão Agrorrural - Governo da Bahia</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const lembrarMe = ref(false)
const mostrarSenha = ref(false)
const form = ref({ email: '', senha: '' })

function valorObrigatorio(valor) {
  return !!valor || 'Campo obrigatório'
}

function emailValido(valor) {
  return /.+@.+\..+/.test(valor) || 'Informe um e-mail válido'
}

function entrar() {
  router.push('/dashboard')
}

function recuperarSenha() {
  router.push('/recuperar-senha')
}

function cadastrar() {
  router.push('/cadastro-usuario')
}
</script>
