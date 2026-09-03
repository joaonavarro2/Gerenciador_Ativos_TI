<template>
  <div class="auth-page">
    <div class="auth-decoration auth-decoration-top" />
    <div class="auth-decoration auth-decoration-bottom" />

    <div class="auth-card auth-card-recovery">
      <div class="auth-brand">
        <div class="auth-brand-icon"><q-icon name="eco" size="30px" /></div>
        <div class="auth-brand-name"><span>Data</span><strong>CAR</strong></div>
      </div>

      <div class="auth-heading">Recuperar senha</div>
      <div class="auth-info-box">
        Informe o e-mail associado à sua conta para receber as instruções de redefinição de senha.
      </div>

      <q-form class="auth-form" @submit.prevent="recuperar">
        <q-input
          v-model="email"
          class="auth-field"
          outlined
          dense
          type="email"
          placeholder="E-mail"
          autocomplete="email"
          :rules="[obrigatorio, emailValido]"
          hide-bottom-space
        >
          <template #prepend><q-icon name="mail_outline" /></template>
        </q-input>

        <q-btn
          unelevated
          no-caps
          icon="replay"
          label="Recuperar Senha"
          type="submit"
          class="auth-submit auth-submit-secondary"
        />
      </q-form>

      <q-btn flat no-caps icon="west" label="Voltar" class="auth-back" @click="voltar" />
      <div class="auth-footer">Sistema de Gestão Agrorrural - Governo da Bahia</div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'

const $q = useQuasar()
const router = useRouter()
const email = ref('')

function obrigatorio(valor) { return !!valor || 'Campo obrigatório' }
function emailValido(valor) { return /.+@.+\..+/.test(valor) || 'Informe um e-mail válido' }
function recuperar() { $q.notify({ type: 'positive', message: 'Se o e-mail estiver cadastrado, enviaremos as instruções de redefinição.' }) }
function voltar() { router.push('/login') }
</script>
