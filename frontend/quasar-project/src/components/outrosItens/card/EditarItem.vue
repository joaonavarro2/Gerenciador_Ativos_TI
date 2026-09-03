<template>
  <q-dialog v-model="aberto" persistent transition-show="fade" transition-hide="fade">
    <q-card class="outro-item-dialog-card">
      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar color="blue-1" text-color="primary" icon="edit" size="48px" />
          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">Editar Item</div>
            <div class="text-subtitle2 text-grey-7">Atualize as informações do item de estoque.</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" @click="fechar" />
      </q-card-section>

      <q-separator />

      <q-form @submit.prevent="salvar">
        <q-card-section class="outro-item-dialog-scroll">
          <div class="titulo-secao">IDENTIFICAÇÃO DO ITEM</div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4"><q-input v-model="form.codigo" outlined dense label="Código *" :rules="[obrigatorio]" hide-bottom-space /></div>
            <div class="col-12 col-md-8"><q-input v-model="form.nome" outlined dense label="Nome do item *" :rules="[obrigatorio]" hide-bottom-space /></div>
            <div class="col-12 col-md-6"><q-select v-model="form.categoria" outlined dense label="Categoria *" :options="categorias" :rules="[obrigatorio]" hide-bottom-space /></div>
            <div class="col-12 col-md-6"><q-select v-model="form.status" outlined dense label="Status *" :options="statusOptions" :rules="[obrigatorio]" hide-bottom-space /></div>
          </div>

          <div class="titulo-secao q-mt-xl">LOCALIZAÇÃO E ESTOQUE</div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-6"><q-select v-model="form.escritorio" outlined dense label="Escritório *" :options="escritorios" :rules="[obrigatorio]" hide-bottom-space /></div>
            <div class="col-12 col-md-6"><q-input v-model="form.localizacao" outlined dense label="Localização *" :rules="[obrigatorio]" hide-bottom-space /></div>
            <div class="col-12 col-md-6"><q-input v-model.number="form.quantidade" outlined dense type="number" min="0" label="Quantidade disponível *" :rules="[quantidadeObrigatoria]" hide-bottom-space /></div>
            <div class="col-12 col-md-6"><q-input v-model.number="form.estoqueMinimo" outlined dense type="number" min="0" label="Estoque mínimo *" :rules="[quantidadeObrigatoria]" hide-bottom-space /></div>
            <div class="col-12"><q-input v-model="form.fornecedor" outlined dense label="Fornecedor" /></div>
          </div>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md outro-item-dialog-actions">
          <q-btn flat no-caps label="Cancelar" @click="fechar" />
          <q-btn color="positive" no-caps icon="check_circle" label="Salvar Alterações" type="submit" :disable="!formValido" />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const aberto = defineModel({ type: Boolean, default: false })
const props = defineProps({ item: { type: Object, default: () => ({}) } })
const emit = defineEmits(['salvar'])

const categorias = ['Material de escritório', 'Suprimentos', 'Mobiliário', 'Outros']
const escritorios = ['Salvador', 'Feira de Santana', 'Vitória da Conquista']
const statusOptions = ['Ativo', 'Inativo', 'Esgotado']
const estadoInicial = () => ({ codigo: '', nome: '', categoria: null, escritorio: null, localizacao: '', quantidade: 0, estoqueMinimo: 0, fornecedor: '', status: 'Ativo' })
const form = ref(estadoInicial())

watch(() => props.item, item => {
  if (item && Object.keys(item).length) form.value = { ...estadoInicial(), ...item }
}, { immediate: true })

const formValido = computed(() => form.value.codigo && form.value.nome && form.value.categoria && form.value.escritorio && form.value.localizacao && form.value.quantidade >= 0 && form.value.estoqueMinimo >= 0 && form.value.status)
function obrigatorio(valor) { return !!valor || 'Campo obrigatório' }
function quantidadeObrigatoria(valor) { return valor >= 0 || 'Informe uma quantidade válida' }
function fechar() { aberto.value = false }
function salvar() { if (!formValido.value) return; emit('salvar', { ...form.value }); fechar() }
</script>

<style scoped>
.outro-item-dialog-card { width: min(760px, 92vw); max-width: 92vw; max-height: 90vh; border-radius: 18px; overflow: hidden; display: flex; flex-direction: column; }
.outro-item-dialog-scroll { overflow-y: auto; flex: 1 1 auto; }
.outro-item-dialog-actions { border-top: 1px solid #e5e7eb; background: #fff; }
.titulo-secao { margin-bottom: 16px; color: #1f2937; font-size: 13px; font-weight: 700; letter-spacing: 0.06em; }
</style>
