<template>
  <div class="auth-page">
    <div class="auth-decoration auth-decoration-top" />
    <div class="auth-decoration auth-decoration-bottom" />

    <div class="auth-card auth-card-recovery">
      <div class="auth-brand auth-brand-compact">
        <div class="auth-brand-icon"><q-icon name="eco" size="30px" /></div>
        <div class="auth-brand-name"><span>Data</span><strong>CAR</strong></div>
      </div>

      <template v-if="senhaAlterada">
        <div class="auth-heading">Senha alterada</div>
        <div class="auth-info-box">
          Sua senha foi redefinida com sucesso. Agora você já pode acessar o sistema com a nova senha.
        </div>
        <q-btn
          unelevated
          no-caps
          icon="login"
          label="Ir para o login"
          class="auth-submit auth-submit-secondary"
          @click="irParaLogin"
        />
      </template>

      <template v-else-if="tokenValido">
        <div class="auth-heading">Criar nova senha</div>
        <div class="auth-subheading">Escolha uma senha segura para proteger sua conta.</div>

        <q-form class="auth-form" @submit.prevent="alterarSenha">
          <q-input
            v-model="form.novaSenha"
            class="auth-field"
            outlined
            dense
            :type="mostrarNovaSenha ? 'text' : 'password'"
            label="Nova senha"
            autocomplete="new-password"
            :rules="[obrigatorio, senhaValida]"
            hide-bottom-space
          >
            <template #prepend><q-icon name="lock_outline" /></template>
            <template #append>
              <q-btn
                flat
                round
                dense
                type="button"
                :icon="mostrarNovaSenha ? 'visibility_off' : 'visibility'"
                class="auth-password-toggle"
                :aria-label="mostrarNovaSenha ? 'Ocultar nova senha' : 'Mostrar nova senha'"
                @click="mostrarNovaSenha = !mostrarNovaSenha"
              />
            </template>
          </q-input>

          <q-input
            v-model="form.confirmacao"
            class="auth-field"
            outlined
            dense
            :type="mostrarConfirmacao ? 'text' : 'password'"
            label="Confirmar nova senha"
            autocomplete="new-password"
            :rules="[obrigatorio, senhasIguais]"
            hide-bottom-space
          >
            <template #prepend><q-icon name="lock_reset" /></template>
            <template #append>
              <q-btn
                flat
                round
                dense
                type="button"
                :icon="mostrarConfirmacao ? 'visibility_off' : 'visibility'"
                class="auth-password-toggle"
                :aria-label="mostrarConfirmacao ? 'Ocultar confirmação' : 'Mostrar confirmação'"
                @click="mostrarConfirmacao = !mostrarConfirmacao"
              />
            </template>
          </q-input>

          <div class="auth-field-hint">
            <q-icon name="info_outline" size="14px" />
            Use pelo menos 8 caracteres, com letras e números.
          </div>

          <q-btn
            unelevated
            no-caps
            icon="check"
            label="Redefinir senha"
            type="submit"
            :loading="enviando"
            class="auth-submit auth-submit-secondary"
          />
        </q-form>
      </template>

      <template v-else>
        <div class="auth-heading">Link inválido ou expirado</div>
        <div class="auth-info-box">
          Este link de redefinição não é mais válido. Solicite um novo link para alterar sua senha.
        </div>
        <q-btn
          unelevated
          no-caps
          icon="replay"
          label="Solicitar novo link"
          class="auth-submit auth-submit-secondary"
          @click="solicitarNovoLink"
        />
      </template>

      <q-btn flat no-caps icon="west" label="Voltar para o login" class="auth-back" @click="irParaLogin" />
      <div class="auth-footer">Sistema de Gestão Agrorrural - Governo da Bahia</div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/boot/axios.js'

const $q = useQuasar()
const route = useRoute()
const router = useRouter()
const form = ref({ novaSenha: '', confirmacao: '' })
const mostrarNovaSenha = ref(false)
const mostrarConfirmacao = ref(false)
const senhaAlterada = ref(false)
const enviando = ref(false)

const tokenValido = computed(() => {
  const token = route.query.token
  return typeof token === 'string' && token.trim().length > 0
})

function obrigatorio(valor) {
  return !!valor || 'Campo obrigatório'
}

function senhaValida(valor) {
  return (valor.length >= 8 && /[A-Za-z]/.test(valor) && /\d/.test(valor))
    || 'Use ao menos 8 caracteres, com letras e números'
}

function senhasIguais(valor) {
  return valor === form.value.novaSenha || 'As senhas precisam ser iguais'
}

async function alterarSenha() {
  if (!form.value.novaSenha || !form.value.confirmacao || form.value.novaSenha !== form.value.confirmacao) {
    $q.notify({ type: 'warning', message: 'Confira os campos antes de continuar.' })
    return
  }

  enviando.value = true
  try {
    await api.post('/auth/redefinir-senha', {
      token: route.query.token,
      novaSenha: form.value.novaSenha,
    })
    senhaAlterada.value = true
    $q.notify({ type: 'positive', message: 'Senha redefinida com sucesso.' })
  } catch (error) {
    const mensagem = error.response?.data?.mensagem || 'O link é inválido ou expirou. Solicite um novo link.'
    $q.notify({ type: 'warning', message: mensagem })
  } finally {
    enviando.value = false
  }
}

function solicitarNovoLink() {
  router.push('/recuperar-senha')
}

function irParaLogin() {
  router.push('/login')
}
</script>
