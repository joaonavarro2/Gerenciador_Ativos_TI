<template>
  <q-page class="profile-page q-pa-lg">
    <div class="profile-content">
      <div class="profile-heading">
        <div>
          <h1 class="profile-title">Meu Perfil</h1>
          <p class="profile-subtitle">Visualize e gerencie as informações da sua conta.</p>
        </div>
        <q-btn unelevated no-caps color="primary" icon="edit" label="Editar Perfil" class="edit-profile-button" @click="abrirEdicao" />
      </div>

      <div class="profile-layout">
        <aside class="profile-sidebar">
          <q-card flat bordered class="profile-summary-card">
            <q-card-section class="profile-summary">
              <q-avatar class="profile-avatar" size="88px">
                <img v-if="perfil.foto" :src="perfil.foto" alt="Foto do perfil" />
                <span v-else>{{ iniciais }}</span>
              </q-avatar>
              <div class="profile-name">{{ perfil.nome || 'Nome do Usuário' }}</div>
              <div class="profile-role">{{ perfil.cargo || 'Função no Sistema' }}</div>
              <q-badge color="positive" text-color="green-10" label="Ativo" class="status-badge" />
            </q-card-section>
            <q-separator />
            <q-list class="profile-meta-list">
              <q-item v-for="item in dadosResumo" :key="item.label">
                <q-item-section avatar><q-icon :name="item.icon" /></q-item-section>
                <q-item-section><q-item-label caption>{{ item.label }}</q-item-label><q-item-label>{{ item.value }}</q-item-label></q-item-section>
              </q-item>
            </q-list>
          </q-card>

          <q-card flat bordered class="security-card">
            <q-card-section>
              <div class="section-title"><q-icon name="lock_outline" /> Segurança</div>
              <p>Atualize sua senha para manter sua conta segura.</p>
              <q-btn outline no-caps color="primary" icon="vpn_key" label="Alterar Senha" class="full-width" @click="alterarSenha" />
            </q-card-section>
          </q-card>
        </aside>

        <main class="profile-main">
          <q-card v-for="secao in secoes" :key="secao.titulo" flat bordered class="info-card">
            <q-card-section>
              <div class="section-title"><q-icon :name="secao.icon" /> {{ secao.titulo }}</div>
              <div v-if="secao.itens" class="info-list">
                <div v-for="item in secao.itens" :key="item.label" class="info-row">
                  <q-icon :name="item.icon" />
                  <div><span>{{ item.label }}</span><strong v-if="item.label !== 'Status da conta'">{{ item.value }}</strong><q-badge v-else color="positive" text-color="green-10" label="Ativo" /></div>
                </div>
              </div>
              <div v-else class="empty-activity"><q-icon name="schedule" /><span>Nenhuma atividade recente registrada.</span></div>
            </q-card-section>
          </q-card>
        </main>
      </div>
    </div>

    <q-dialog v-model="dialogEdicao">
      <q-card class="edit-dialog">
        <q-card-section class="row items-center justify-between"><div class="text-h6">Editar Perfil</div><q-btn flat round dense icon="close" v-close-popup /></q-card-section>
        <q-card-section class="edit-form">
          <q-input v-model="edicao.nome" outlined label="Nome completo" />
          <q-input v-model="edicao.email" outlined type="email" label="E-mail" />
          <q-input v-model="edicao.cargo" outlined label="Função no sistema" />
          <q-input v-model="edicao.telefone" outlined label="Telefone" />
          <q-input v-model="edicao.escritorio" outlined label="Escritório / Unidade" />
          <q-input v-model="edicao.departamento" outlined label="Departamento" />
          <q-input v-model="edicao.foto" outlined label="URL da foto de perfil" hint="Cole o endereço de uma imagem para atualizar o avatar." />
        </q-card-section>
        <q-card-actions align="right"><q-btn flat no-caps label="Cancelar" v-close-popup /><q-btn unelevated no-caps color="primary" label="Salvar alterações" @click="salvar" /></q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog v-model="dialogSenha">
      <q-card class="edit-dialog">
        <q-card-section class="row items-center justify-between">
          <div>
            <div class="text-h6">Alterar Senha</div>
            <div class="text-caption text-grey-7">Crie uma nova senha para sua conta.</div>
          </div>
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-section>
        <q-form @submit.prevent="salvarSenha">
          <q-card-section class="edit-form">
            <q-input v-model="senha.atual" outlined :type="mostrarSenhaAtual ? 'text' : 'password'" label="Senha atual" :rules="[obrigatorio]">
              <template #append><q-icon :name="mostrarSenhaAtual ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="mostrarSenhaAtual = !mostrarSenhaAtual" /></template>
            </q-input>
            <q-input v-model="senha.nova" outlined :type="mostrarSenhaNova ? 'text' : 'password'" label="Nova senha" hint="Use pelo menos 6 caracteres." :rules="[obrigatorio, senhaValida]">
              <template #append><q-icon :name="mostrarSenhaNova ? 'visibility_off' : 'visibility'" class="cursor-pointer" @click="mostrarSenhaNova = !mostrarSenhaNova" /></template>
            </q-input>
            <q-input v-model="senha.confirmacao" outlined type="password" label="Confirmar nova senha" :rules="[obrigatorio, senhaConfirmada]" />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat no-caps label="Cancelar" v-close-popup />
            <q-btn unelevated no-caps color="primary" label="Salvar nova senha" type="submit" />
          </q-card-actions>
        </q-form>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useQuasar } from 'quasar'
import { usuarioAtual } from '@/services/auth.js'

const $q = useQuasar()
const dialogEdicao = ref(false)
const dialogSenha = ref(false)
const mostrarSenhaAtual = ref(false)
const mostrarSenhaNova = ref(false)
const sessao = usuarioAtual()
const perfil = reactive({
  nome: sessao?.nomeCompleto || localStorage.getItem('usuario_nome') || 'Usuário',
  email: sessao?.email || localStorage.getItem('usuario_email') || '',
  cargo: sessao?.funcao || localStorage.getItem('usuario_cargo') || 'USUÁRIO',
  telefone: localStorage.getItem('usuario_telefone') || '(71) 99999-1234',
  id: localStorage.getItem('usuario_id') || 'ST-2024-0187',
  escritorio: localStorage.getItem('usuario_escritorio') || 'Unidade Salvador',
  departamento: localStorage.getItem('usuario_departamento') || 'Tecnologia da Informação',
  foto: localStorage.getItem('usuario_foto') || 'https://i.pravatar.cc/176?img=47'
})
const edicao = reactive({ nome: '', email: '', cargo: '', telefone: '', escritorio: '', departamento: '', foto: '' })
const senha = reactive({ atual: '', nova: '', confirmacao: '' })
const iniciais = computed(() => (perfil.nome || '?').split(' ').filter(Boolean).slice(0, 2).map((parte) => parte[0]).join('').toUpperCase())
const dadosResumo = computed(() => [{ icon: 'tag', label: 'ID do Servidor', value: perfil.id || '—' }, { icon: 'business', label: 'Escritório', value: perfil.escritorio || '—' }, { icon: 'account_tree', label: 'Departamento', value: perfil.departamento || '—' }])
const secoes = computed(() => [
  { icon: 'person_outline', titulo: 'Informações Pessoais', itens: [{ icon: 'person_outline', label: 'Nome completo', value: perfil.nome || '—' }, { icon: 'mail_outline', label: 'E-mail', value: perfil.email || '—' }, { icon: 'phone', label: 'Telefone', value: perfil.telefone || '—' }] },
  { icon: 'business_center', titulo: 'Informações Organizacionais', itens: [{ icon: 'business', label: 'Escritório / Unidade', value: perfil.escritorio || '—' }, { icon: 'account_tree', label: 'Departamento', value: perfil.departamento || '—' }, { icon: 'shield', label: 'Função no sistema', value: perfil.cargo || '—' }, { icon: 'check_circle_outline', label: 'Status da conta', value: 'Ativo' }] },
  { icon: 'schedule', titulo: 'Atividade Recente da Conta' }
])
function abrirEdicao () { Object.assign(edicao, { nome: perfil.nome, email: perfil.email, cargo: perfil.cargo, telefone: perfil.telefone, escritorio: perfil.escritorio, departamento: perfil.departamento, foto: perfil.foto }); dialogEdicao.value = true }
function salvar () { Object.assign(perfil, edicao); Object.entries(edicao).forEach(([chave, valor]) => localStorage.setItem(`usuario_${chave}`, valor)); dialogEdicao.value = false; $q.notify({ type: 'positive', message: 'Perfil atualizado com sucesso.' }) }
function alterarSenha () { Object.assign(senha, { atual: '', nova: '', confirmacao: '' }); dialogSenha.value = true }
function obrigatorio (valor) { return Boolean(valor) || 'Preencha este campo.' }
function senhaValida (valor) { return valor.length >= 6 || 'A senha deve ter pelo menos 6 caracteres.' }
function senhaConfirmada (valor) { return valor === senha.nova || 'As senhas não conferem.' }
function salvarSenha () { localStorage.setItem('usuario_senha', senha.nova); dialogSenha.value = false; $q.notify({ type: 'positive', message: 'Senha alterada com sucesso.' }) }
</script>

<style scoped>
.profile-page { background: #f5f7fa; }
.profile-content { width: min(100%, 1170px); margin: 0 auto; }
.profile-heading { display: flex; align-items: flex-start; justify-content: space-between; gap: 24px; margin-bottom: 22px; }
.profile-title { margin: 0; color: #101828; font-size: 27px; font-weight: 700; }
.profile-subtitle { margin: 7px 0 0; color: #667085; font-size: 14px; }
.edit-profile-button { min-height: 38px; border-radius: 10px; box-shadow: 0 5px 12px rgba(15, 93, 99, .2); font-weight: 600; }
.profile-layout { display: grid; grid-template-columns: 290px minmax(0, 1fr); gap: 22px; align-items: start; }
.profile-sidebar, .profile-main { display: grid; gap: 20px; }
.profile-summary-card, .security-card, .info-card { border: 1px solid #edf0f3; border-radius: 16px; background: #fff; box-shadow: 0 4px 13px rgba(16, 24, 40, .06); }
.profile-summary { display: flex; flex-direction: column; align-items: center; padding: 42px 24px 24px; text-align: center; }
.profile-avatar { display: flex; align-items: center; justify-content: center; color: #fff; background: linear-gradient(145deg, #00747b, #009aa7); font-size: 28px; font-weight: 700; box-shadow: 0 8px 18px rgba(0, 116, 123, .2); }
.profile-name { margin-top: 18px; color: #c3cad5; font-size: 17px; font-weight: 700; }
.profile-role { margin-top: 5px; color: #c3cad5; font-size: 13px; }
.status-badge { margin-top: 14px; padding: 4px 11px; border-radius: 12px; font-weight: 600; }
.profile-meta-list { padding: 12px 18px 18px; }
.profile-meta-list .q-item { min-height: 44px; padding: 5px 0; color: #b8c1cf; }
.profile-meta-list .q-item__section--avatar { min-width: 25px; color: #aeb8c7; }
.profile-meta-list .q-item__label--caption { color: #b8c1cf; font-size: 12px; }
.profile-meta-list .q-item__label:not(.q-item__label--caption) { color: #c9d0db; font-size: 13px; }
.security-card .q-card__section, .info-card .q-card__section { padding: 25px; }
.security-card p { margin: 13px 0 17px; color: #667085; font-size: 13px; line-height: 1.6; }
.security-card .q-btn { min-height: 42px; border-radius: 9px; font-weight: 600; }
.section-title { display: flex; align-items: center; gap: 10px; color: #101828; font-size: 15px; font-weight: 700; }
.section-title .q-icon { color: #00747b; font-size: 19px; }
.info-list { margin-top: 17px; border-top: 1px solid #edf0f3; }
.info-row { display: flex; align-items: flex-start; gap: 13px; min-height: 67px; padding: 14px 0 11px; border-bottom: 1px solid #edf0f3; }
.info-row > .q-icon { flex: 0 0 16px; margin-top: 2px; color: #98a2b3; font-size: 16px; }
.info-row > div { display: flex; flex-direction: column; gap: 7px; }
.info-row span { color: #8d99aa; font-size: 11px; font-weight: 600; letter-spacing: .05em; text-transform: uppercase; }
.info-row strong { color: #101828; font-size: 14px; font-weight: 500; }
.info-row .q-badge { align-self: flex-start; padding: 4px 11px; border-radius: 12px; font-weight: 600; }
.empty-activity { display: flex; min-height: 115px; flex-direction: column; align-items: center; justify-content: center; gap: 10px; color: #98a2b3; font-size: 13px; }
.empty-activity .q-icon { padding: 9px; border-radius: 12px; color: #cbd2dc; background: #f3f5f8; font-size: 26px; }
.edit-dialog { width: min(460px, calc(100vw - 32px)); border-radius: 14px; }
.edit-form { display: grid; gap: 16px; }
@media (max-width: 800px) { .profile-layout { grid-template-columns: 1fr; } .profile-sidebar { grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); } }
@media (max-width: 560px) { .profile-page { padding: 18px 14px !important; } .profile-heading { flex-direction: column; } .edit-profile-button { align-self: flex-start; } .profile-sidebar { grid-template-columns: 1fr; } }
</style>
