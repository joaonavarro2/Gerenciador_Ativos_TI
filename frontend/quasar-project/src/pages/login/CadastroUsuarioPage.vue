<template>
  <div class="auth-page">
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
          <q-input v-model="form.senha" class="auth-field" outlined dense :type="mostrarSenha ? 'text' : 'password'" placeholder="Senha" autocomplete="new-password" :rules="[obrigatorio]" hide-bottom-space>
            <template #prepend><q-icon name="lock_outline" /></template>
            <template #append><q-btn flat round dense :icon="mostrarSenha ? 'visibility_off' : 'visibility'" class="auth-password-toggle" :aria-label="mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'" @click="mostrarSenha = !mostrarSenha" /></template>
          </q-input>
          <q-input v-model="form.confirmarSenha" class="auth-field" outlined dense :type="mostrarConfirmacao ? 'text' : 'password'" placeholder="Confirme a senha" autocomplete="new-password" :rules="[obrigatorio, senhasIguais]" hide-bottom-space>
            <template #prepend><q-icon name="lock_outline" /></template>
            <template #append><q-btn flat round dense :icon="mostrarConfirmacao ? 'visibility_off' : 'visibility'" class="auth-password-toggle" :aria-label="mostrarConfirmacao ? 'Ocultar senha' : 'Mostrar senha'" @click="mostrarConfirmacao = !mostrarConfirmacao" /></template>
          </q-input>
        </div>

        <q-select v-model="form.funcao" class="auth-field" outlined dense label="Função" :options="funcoes" :rules="[obrigatorio]" hide-bottom-space>
          <template #prepend><q-icon name="badge" /></template>
        </q-select>
        <div class="auth-field-hint"><q-icon name="info_outline" /> As funções disponíveis serão carregadas do backend.</div>

        <q-btn unelevated no-caps icon="person_add" label="Cadastrar Usuário" type="submit" class="auth-submit auth-submit-register" />
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

const $q = useQuasar()
const router = useRouter()
const mostrarSenha = ref(false)
const mostrarConfirmacao = ref(false)
const funcoes = ['Administrador', 'Gestor', 'Técnico', 'Operador']
const form = ref({ nome: '', email: '', senha: '', confirmarSenha: '', funcao: null })

function obrigatorio(valor) { return !!valor || 'Campo obrigatório' }
function emailValido(valor) { return /.+@.+\..+/.test(valor) || 'Informe um e-mail válido' }
function senhasIguais(valor) { return valor === form.value.senha || 'As senhas precisam ser iguais' }
function cadastrar() { $q.notify({ type: 'positive', message: 'Usuário cadastrado com sucesso.' }); router.push('/login') }
function voltar() { router.push('/login') }
</script>
