<template>
  <q-card flat bordered class="bens-header outros-itens-header">
    <div class="row items-start justify-between q-mb-lg">
      <div>
        <div class="text-h4 text-weight-bold">Outros Itens</div>
        <div class="text-subtitle1 text-grey-7 q-mt-xs">
          Gerencie itens de estoque que não são classificados como bens patrimoniais.
        </div>
      </div>

      <q-btn
        color="positive"
        icon="add"
        label="Novo Item"
        no-caps
        class="outros-itens-btn-novo"
        @click="emit('novo-item')"
      />
    </div>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-md">
        <q-input
          v-model="busca"
          outlined
          dense
          clearable
          placeholder="Buscar por nome, categoria, código ou fornecedor..."
          class="outros-itens-busca"
        >
          <template #prepend><q-icon name="search" /></template>
        </q-input>
      </div>

      <div class="col-12 col-md-auto">
        <q-select
          v-model="categoria"
          outlined
          dense
          clearable
          label="Categoria"
          :options="opcoesCategorias"
          class="outros-itens-filtro"
        />
      </div>

      <div class="col-12 col-md-auto">
        <q-select
          v-model="escritorio"
          outlined
          dense
          clearable
          label="Escritório"
          :options="opcoesEscritorios"
          class="outros-itens-filtro"
        />
      </div>

      <div class="col-12 col-md-auto">
        <q-select
          v-model="status"
          outlined
          dense
          clearable
          label="Status"
          :options="opcoesStatus"
          class="outros-itens-filtro"
        />
      </div>
    </div>

    <div v-if="possuiFiltros" class="row items-center q-gutter-sm q-mt-lg">
      <div class="text-caption text-grey-7">Filtros aplicados:</div>

      <q-chip v-if="props.filtros.busca" removable color="blue-1" text-color="primary" icon="search" @remove="busca = ''">
        Pesquisa: {{ props.filtros.busca }}
      </q-chip>
      <q-chip v-if="props.filtros.categoria" removable color="blue-1" text-color="primary" icon="category" @remove="categoria = null">
        Categoria: {{ props.filtros.categoria }}
      </q-chip>
      <q-chip v-if="props.filtros.escritorio" removable color="blue-1" text-color="primary" icon="business" @remove="escritorio = null">
        Escritório: {{ props.filtros.escritorio }}
      </q-chip>
      <q-chip v-if="props.filtros.status" removable color="blue-1" text-color="primary" icon="verified" @remove="status = null">
        Status: {{ props.filtros.status }}
      </q-chip>
    </div>
  </q-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  filtros: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['update:filtros', 'novo-item'])

const opcoesCategorias = ['Material de escritório', 'Suprimentos', 'Mobiliário', 'Outros']
const opcoesEscritorios = ['Salvador', 'Feira de Santana', 'Vitória da Conquista']
const opcoesStatus = ['Ativo', 'Inativo', 'Esgotado']

const busca = computed({
  get: () => props.filtros.busca || '',
  set: value => emit('update:filtros', { ...props.filtros, busca: value || '' }),
})

const categoria = computed({
  get: () => props.filtros.categoria || null,
  set: value => emit('update:filtros', { ...props.filtros, categoria: value || null }),
})

const escritorio = computed({
  get: () => props.filtros.escritorio || null,
  set: value => emit('update:filtros', { ...props.filtros, escritorio: value || null }),
})

const status = computed({
  get: () => props.filtros.status || null,
  set: value => emit('update:filtros', { ...props.filtros, status: value || null }),
})

const possuiFiltros = computed(() =>
  Object.values(props.filtros).some(value => value !== null && value !== ''),
)
</script>
