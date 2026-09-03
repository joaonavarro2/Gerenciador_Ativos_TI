<template>
  <q-dialog v-model="aberto" persistent transition-show="fade" transition-hide="fade">
    <q-card class="outro-item-dialog-card">
      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar color="green-1" text-color="teal" icon="layers" size="48px" />
          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">Novo Item</div>
            <div class="text-subtitle2 text-grey-7">Registre um novo item de estoque no sistema.</div>
          </div>
        </div>
        <q-btn flat round dense icon="close" @click="fechar" />
      </q-card-section>

      <q-separator />

      <q-form @submit.prevent="salvar">
        <q-card-section class="outro-item-dialog-scroll">
          <div class="titulo-secao">IDENTIFICAÇÃO DO ITEM</div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-4">
              <q-input v-model="form.codigo" outlined dense label="Código *" placeholder="OUT-000" :rules="[obrigatorio]" hide-bottom-space />
            </div>
            <div class="col-12 col-md-8">
              <q-input v-model="form.nome" outlined dense label="Nome do item *" placeholder="Ex: Papel A4 - Resma" :rules="[obrigatorio]" hide-bottom-space />
            </div>
            <div class="col-12 col-md-6">
              <q-select v-model="form.categoria" outlined dense label="Categoria *" :options="categorias" :rules="[obrigatorio]" hide-bottom-space />
            </div>
            <div class="col-12 col-md-6">
              <q-select v-model="form.status" outlined dense label="Status *" :options="statusOptions" :rules="[obrigatorio]" hide-bottom-space />
            </div>
          </div>

          <div class="titulo-secao q-mt-xl">LOCALIZAÇÃO E ESTOQUE</div>
          <div class="row q-col-gutter-md">
            <div class="col-12 col-md-6">
              <q-select v-model="form.escritorio" outlined dense label="Escritório *" :options="escritorios" :rules="[obrigatorio]" hide-bottom-space />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="form.localizacao" outlined dense label="Localização *" placeholder="Ex: Almoxarifado Central" :rules="[obrigatorio]" hide-bottom-space />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model.number="form.quantidade" outlined dense type="number" min="0" label="Quantidade disponível *" :rules="[quantidadeObrigatoria]" hide-bottom-space />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model.number="form.estoqueMinimo" outlined dense type="number" min="0" label="Estoque mínimo *" :rules="[quantidadeObrigatoria]" hide-bottom-space />
            </div>
          </div>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md outro-item-dialog-actions">
          <q-btn flat no-caps label="Cancelar" @click="fechar" />
          <q-btn color="positive" no-caps icon="check_circle" label="Salvar Item" type="submit" :disable="!formValido" />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'

const aberto = defineModel({ type: Boolean, default: false })
const emit = defineEmits(['salvar'])

const categorias = ['Monitor','Mouse','Teclado','Cabo','Fonte','Estabilizador','No-break','Roteador','Switch','Hub','Impressora','Scanner','Nobreak','Estabilizador','Cabo de rede','Cabo HDMI','Cabo VGA']
const escritorios = ['Alagoinhas',
  'Amargosa',
  'Barreiras',
  'Bom Jesus da Lapa',
  'Caetité',
  'Salvador',
  'Cruz das Almas',
  'Eunápolis',
  'Ribeira do Pombal',
  'Feira de Santana',
  'Irecê',
  'Itaberaba',
  'Itabuna',
  'Itapetinga',
  'Jacobina',
  'Jequié',
  'Juazeiro',
  'Macaúbas',
  'Paulo Afonso',
  'Riachão do Jacuípe',
  'Santa Maria da Vitória',
  'Seabra',
  'Senhor do Bonfim',
  'Serrinha',
  'Teixeira de Freitas',
  'Valença',
  'Vitória da Conquista',]
const statusOptions = ['Ativo', 'Inativo', 'Esgotado']

const estadoInicial = () => ({
  codigo: '',
  nome: '',
  categoria: null,
  escritorio: null,
  localizacao: '',
  quantidade: 0,
  estoqueMinimo: 0,
  fornecedor: '',
  status: 'Ativo',
})

const form = ref(estadoInicial())
const formValido = computed(() =>
  form.value.codigo && form.value.nome && form.value.categoria && form.value.escritorio
  && form.value.localizacao && form.value.quantidade >= 0 && form.value.estoqueMinimo >= 0 && form.value.status,
)

function obrigatorio(valor) {
  return !!valor || 'Campo obrigatório'
}

function quantidadeObrigatoria(valor) {
  return valor >= 0 || 'Informe uma quantidade válida'
}

function fechar() {
  aberto.value = false
  form.value = estadoInicial()
}

function salvar() {
  if (!formValido.value) return
  emit('salvar', { ...form.value, id: Date.now() })
  fechar()
}
</script>

<style scoped>
.outro-item-dialog-card {
  width: min(760px, 92vw);
  max-width: 92vw;
  max-height: 90vh;
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.outro-item-dialog-scroll {
  overflow-y: auto;
  flex: 1 1 auto;
}

.outro-item-dialog-actions {
  border-top: 1px solid #e5e7eb;
  background: #fff;
}

.titulo-secao {
  margin-bottom: 16px;
  color: #1f2937;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.06em;
}
</style>
