<template>
  <q-page class="profile-page q-pa-lg">
    <div class="profile-content">
      <header class="profile-heading">
        <div>
          <h1 class="profile-title">Meu Perfil</h1>
          <p class="profile-subtitle">Consulte e atualize seus dados cadastrais.</p>
        </div>
        <q-btn
          unelevated
          no-caps
          color="primary"
          icon="edit"
          label="Editar informações"
          :disable="carregando || !perfil.id"
          @click="abrirEdicao"
        />
      </header>

      <q-banner v-if="erroCarregamento" rounded class="bg-red-1 text-negative q-mb-md">
        {{ erroCarregamento }}
        <template #action>
          <q-btn flat label="Tentar novamente" @click="carregarPerfil" />
        </template>
      </q-banner>

      <div v-if="carregando" class="profile-loading">
        <q-spinner color="primary" size="32px" />
        <span>Carregando seus dados...</span>
      </div>

      <div v-else-if="perfil.id" class="profile-layout">
        <aside class="profile-sidebar">
          <q-card flat bordered class="profile-summary-card">
            <q-card-section class="profile-summary">
              <q-avatar class="profile-avatar" size="88px">{{ iniciais }}</q-avatar>
              <div class="profile-name">{{ perfil.nomeCompleto }}</div>
              <div class="profile-email">{{ perfil.email }}</div>
              <q-badge
                :color="perfil.status === 'ATIVO' ? 'positive' : 'grey'"
                text-color="green-10"
                class="status-badge"
              >
                {{ rotuloStatus }}
              </q-badge>
            </q-card-section>
            <q-separator />
            <q-list class="profile-meta-list">
              <q-item>
                <q-item-section avatar><q-icon name="tag" /></q-item-section>
                <q-item-section>
                  <q-item-label caption>Identificador</q-item-label>
                  <q-item-label>{{ perfil.id }}</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-card>

          <q-card flat bordered class="security-card">
            <q-card-section>
              <div class="section-title"><q-icon name="lock_outline" /> Segurança da conta</div>
              <p>Altere sua senha usando a validação segura da sua conta.</p>
              <q-btn
                outline
                no-caps
                color="primary"
                icon="vpn_key"
                label="Alterar senha"
                class="full-width"
                @click="abrirAlteracaoSenha"
              />
            </q-card-section>
          </q-card>
        </aside>

        <main class="profile-main">
          <q-card flat bordered class="info-card">
            <q-card-section>
              <div class="section-title">
                <q-icon name="person_outline" /> Informações pessoais
              </div>
              <div class="info-list">
                <div class="info-row">
                  <q-icon name="person_outline" />
                  <div>
                    <span>Nome completo</span>
                    <strong>{{ perfil.nomeCompleto }}</strong>
                  </div>
                </div>
                <div class="info-row">
                  <q-icon name="mail_outline" />
                  <div>
                    <span>E-mail</span>
                    <strong>{{ perfil.email }}</strong>
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>

          <q-card flat bordered class="info-card">
            <q-card-section>
              <div class="section-title">
                <q-icon name="manage_accounts" /> Informações da conta
              </div>
              <div class="info-list">
                <div class="info-row">
                  <q-icon name="badge" />
                  <div>
                    <span>Perfil de acesso</span>
                    <strong>{{ rotuloFuncao }}</strong>
                  </div>
                </div>
                <div class="info-row">
                  <q-icon name="check_circle_outline" />
                  <div>
                    <span>Situação da conta</span>
                    <strong>{{ rotuloStatus }}</strong>
                  </div>
                </div>
              </div>
            </q-card-section>
          </q-card>
        </main>
      </div>
    </div>

    <q-dialog v-model="dialogEdicao">
      <q-card class="edit-dialog">
        <q-card-section class="row items-center justify-between">
          <div class="text-h6">Editar informações</div>
          <q-btn flat round dense icon="close" v-close-popup aria-label="Fechar" />
        </q-card-section>
        <q-form class="edit-form" @submit.prevent="salvarPerfil">
          <q-card-section class="edit-fields">
            <q-input
              v-model="edicao.nomeCompleto"
              outlined
              label="Nome completo"
              maxlength="150"
              autocomplete="name"
              :rules="[nomeObrigatorio]"
            />
            <q-input
              v-model="edicao.email"
              outlined
              type="email"
              label="E-mail"
              maxlength="150"
              autocomplete="email"
              :rules="[emailObrigatorio, emailValido]"
            />
            <p v-if="erroFormulario" class="text-negative">{{ erroFormulario }}</p>
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat no-caps label="Cancelar" v-close-popup />
            <q-btn
              unelevated
              no-caps
              color="primary"
              label="Salvar alterações"
              type="submit"
              :loading="salvandoPerfil"
            />
          </q-card-actions>
        </q-form>
      </q-card>
    </q-dialog>

    <q-dialog v-model="dialogSenha">
      <q-card class="edit-dialog">
        <q-card-section class="row items-center justify-between">
          <div>
            <div class="text-h6">Alterar senha</div>
            <div class="text-caption text-grey-7">A senha deve ter entre 8 e 100 caracteres.</div>
          </div>
          <q-btn flat round dense icon="close" v-close-popup aria-label="Fechar" />
        </q-card-section>
        <q-form @submit.prevent="salvarSenha">
          <q-card-section class="edit-fields">
            <q-input
              v-model="senha.atual"
              outlined
              type="password"
              label="Senha atual"
              autocomplete="current-password"
              :rules="[obrigatorio]"
            />
            <q-input
              v-model="senha.nova"
              outlined
              type="password"
              label="Nova senha"
              autocomplete="new-password"
              :rules="[obrigatorio, senhaValida]"
            />
            <q-input
              v-model="senha.confirmacao"
              outlined
              type="password"
              label="Confirmar nova senha"
              autocomplete="new-password"
              :rules="[obrigatorio, senhaConfirmada]"
            />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat no-caps label="Cancelar" v-close-popup />
            <q-btn
              unelevated
              no-caps
              color="primary"
              label="Salvar nova senha"
              type="submit"
              :loading="salvandoSenha"
            />
          </q-card-actions>
        </q-form>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
import { api } from '@/boot/axios.js'
import { atualizarToken, atualizarUsuario, clearSession } from '@/services/auth.js'

const $q = useQuasar()
const router = useRouter()
const carregando = ref(true)
const salvandoPerfil = ref(false)
const salvandoSenha = ref(false)
const erroCarregamento = ref('')
const erroFormulario = ref('')
const dialogEdicao = ref(false)
const dialogSenha = ref(false)
const perfil = reactive({ id: null, nomeCompleto: '', email: '', funcao: '', status: '' })
const edicao = reactive({ nomeCompleto: '', email: '' })
const senha = reactive({ atual: '', nova: '', confirmacao: '' })

const iniciais = computed(() =>
  perfil.nomeCompleto
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((parte) => parte[0])
    .join('')
    .toUpperCase()
)
const rotuloFuncao = computed(() => {
  if (perfil.funcao === 'ADMINISTRADOR GERAL' || perfil.funcao === 'ADMINISTRADOR_GERAL') {
    return 'Administrador Geral'
  }
  return perfil.funcao === 'ADMINISTRADOR' ? 'Administrador' : '—'
})
const rotuloStatus = computed(() => (perfil.status === 'ATIVO' ? 'Ativa' : 'Inativa'))

function mensagemErro(error, fallback) {
  return error.response?.data?.mensagem || error.response?.data?.message || fallback
}

async function carregarPerfil() {
  carregando.value = true
  erroCarregamento.value = ''
  try {
    const { data } = await api.get('/auth/me')
    Object.assign(perfil, data)
    atualizarUsuario(data)
  } catch (error) {
    erroCarregamento.value = mensagemErro(error, 'Não foi possível carregar os dados do perfil.')
  } finally {
    carregando.value = false
  }
}

function abrirEdicao() {
  Object.assign(edicao, { nomeCompleto: perfil.nomeCompleto, email: perfil.email })
  erroFormulario.value = ''
  dialogEdicao.value = true
}

function nomeObrigatorio(valor) {
  return Boolean(valor?.trim()) || 'Informe seu nome completo.'
}

function emailObrigatorio(valor) {
  return Boolean(valor?.trim()) || 'Informe seu e-mail.'
}

function emailValido(valor) {
  return /.+@.+\..+/.test(valor || '') || 'Informe um e-mail válido.'
}

async function salvarPerfil() {
  erroFormulario.value = ''
  const dados = {
    nomeCompleto: edicao.nomeCompleto.trim(),
    email: edicao.email.trim(),
  }
  if (!dados.nomeCompleto || emailValido(dados.email) !== true) {
    erroFormulario.value = 'Informe um nome e um e-mail válidos.'
    return
  }

  salvandoPerfil.value = true
  try {
    const { data } = await api.put('/auth/me', dados)
    if (!data.token || !data.usuario) {
      throw new Error('A resposta de atualização do perfil está incompleta.')
    }
    atualizarToken(data)
    Object.assign(perfil, data.usuario)
    dialogEdicao.value = false
    $q.notify({ type: 'positive', message: 'Perfil atualizado com sucesso.' })
  } catch (error) {
    erroFormulario.value = mensagemErro(error, 'Não foi possível atualizar o perfil.')
  } finally {
    salvandoPerfil.value = false
  }
}

function abrirAlteracaoSenha() {
  Object.assign(senha, { atual: '', nova: '', confirmacao: '' })
  dialogSenha.value = true
}

function obrigatorio(valor) {
  return Boolean(valor) || 'Preencha este campo.'
}

function senhaValida(valor) {
  return (valor?.length >= 8 && valor.length <= 100) || 'A senha deve ter entre 8 e 100 caracteres.'
}

function senhaConfirmada(valor) {
  return valor === senha.nova || 'As senhas não conferem.'
}

async function salvarSenha() {
  salvandoSenha.value = true
  try {
    await api.put('/auth/alterar-senha', {
      senhaAtual: senha.atual,
      novaSenha: senha.nova,
      confirmarSenha: senha.confirmacao,
    })
    dialogSenha.value = false
    clearSession()
    $q.notify({ type: 'positive', message: 'Senha alterada. Entre novamente com a nova senha.' })
    await router.replace('/login')
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: mensagemErro(error, 'Não foi possível alterar a senha.'),
    })
  } finally {
    salvandoSenha.value = false
  }
}

onMounted(carregarPerfil)
</script>

<style scoped>
.profile-page {
  background: #f5f7fa;
}

.profile-content {
  width: min(100%, 1170px);
  margin: 0 auto;
}

.profile-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 22px;
}

.profile-title {
  margin: 0;
  color: #101828;
  font-size: 27px;
  font-weight: 700;
}

.profile-subtitle {
  margin: 7px 0 0;
  color: #667085;
  font-size: 14px;
}

.profile-loading {
  display: flex;
  min-height: 240px;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #667085;
}

.profile-layout {
  display: grid;
  grid-template-columns: 290px minmax(0, 1fr);
  gap: 22px;
  align-items: start;
}

.profile-sidebar,
.profile-main {
  display: grid;
  gap: 20px;
}

.profile-summary-card,
.security-card,
.info-card {
  border: 1px solid #edf0f3;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 13px rgba(16, 24, 40, 0.06);
}

.profile-summary {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 36px 24px 24px;
  text-align: center;
}

.profile-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(145deg, #0f5d63, #009aa7);
  font-size: 28px;
  font-weight: 700;
}

.profile-name {
  margin-top: 18px;
  color: #101828;
  font-size: 17px;
  font-weight: 700;
}

.profile-email {
  margin-top: 5px;
  color: #667085;
  font-size: 13px;
  overflow-wrap: anywhere;
}

.status-badge {
  margin-top: 14px;
  padding: 4px 11px;
  border-radius: 12px;
  font-weight: 600;
}

.profile-meta-list {
  padding: 12px 18px 18px;
}

.profile-meta-list .q-item {
  min-height: 44px;
  padding: 5px 0;
  color: #667085;
}

.profile-meta-list .q-item__section--avatar {
  min-width: 25px;
  color: #0f5d63;
}

.security-card .q-card__section,
.info-card .q-card__section {
  padding: 25px;
}

.security-card p {
  margin: 13px 0 17px;
  color: #667085;
  font-size: 13px;
  line-height: 1.6;
}

.security-card .q-btn {
  min-height: 42px;
  border-radius: 9px;
  font-weight: 600;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #101828;
  font-size: 15px;
  font-weight: 700;
}

.section-title .q-icon {
  color: #0f5d63;
  font-size: 19px;
}

.info-list {
  margin-top: 17px;
  border-top: 1px solid #edf0f3;
}

.info-row {
  display: flex;
  align-items: flex-start;
  gap: 13px;
  min-height: 67px;
  padding: 14px 0 11px;
  border-bottom: 1px solid #edf0f3;
}

.info-row > .q-icon {
  flex: 0 0 16px;
  margin-top: 2px;
  color: #667085;
  font-size: 16px;
}

.info-row > div {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.info-row span {
  color: #667085;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.info-row strong {
  color: #101828;
  font-size: 14px;
  font-weight: 500;
  overflow-wrap: anywhere;
}

.edit-dialog {
  width: min(460px, calc(100vw - 32px));
  border-radius: 14px;
}

.edit-form {
  display: grid;
}

.edit-fields {
  display: grid;
  gap: 16px;
}

@media (max-width: 800px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }

  .profile-sidebar {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .profile-page {
    padding: 18px 14px !important;
  }

  .profile-heading {
    flex-direction: column;
  }

  .profile-sidebar {
    grid-template-columns: 1fr;
  }
}
</style>
