<template>
  <q-card flat bordered class="movimentacoes-header">
    <div class="row items-start justify-between q-mb-lg">
      <div class="movimentacoes-header-title">
        <div class="movimentacoes-title text-h4 text-weight-bold">Movimentações</div>
        <div class="movimentacoes-subtitle text-subtitle1 text-grey-7 q-mt-xs">
          Registro e controle de transferências entre unidades e departamentos.
        </div>
      </div>

      <div class="movimentacoes-header-actions">
        <q-btn
          color="warning"
          icon="add"
          label="Nova Movimentação"
          no-caps
          unelevated
          class="movimentacoes-btn-nova"
          @click="novaMovimentacao"
        />
      </div>
    </div>

    <div class="row items-center q-gutter-md movimentacoes-resumo">
      <q-card flat bordered class="movimentacoes-resumo-card">
        <div class="movimentacoes-resumo-content">
          <div class="movimentacoes-resumo-numero movimentacoes-resumo-total">
            {{ resumoMovimentacoes.total }}
          </div>
          <div class="movimentacoes-resumo-label">Total</div>
        </div>
      </q-card>

      <q-card flat bordered class="movimentacoes-resumo-card">
        <div class="movimentacoes-resumo-content">
          <div class="movimentacoes-resumo-numero movimentacoes-resumo-aberto">
            {{ resumoMovimentacoes.emAberto }}
          </div>
          <div class="movimentacoes-resumo-label">Em Aberto</div>
        </div>
      </q-card>

      <q-card flat bordered class="movimentacoes-resumo-card">
        <div class="movimentacoes-resumo-content">
          <div class="movimentacoes-resumo-numero movimentacoes-resumo-pendente">
            {{ resumoMovimentacoes.pendente }}
          </div>
          <div class="movimentacoes-resumo-label">Pendente</div>
        </div>
      </q-card>

      <q-card flat bordered class="movimentacoes-resumo-card">
        <div class="movimentacoes-resumo-content">
          <div class="movimentacoes-resumo-numero movimentacoes-resumo-concluido">
            {{ resumoMovimentacoes.concluido }}
          </div>
          <div class="movimentacoes-resumo-label">Concluído</div>
        </div>
      </q-card>
    </div>

    <div class="row q-col-gutter-md q-mt-lg movimentacoes-filtros">
      <div class="col">
        <q-input
          v-model="busca"
          outlined
          dense
          clearable
          placeholder="Buscar por ID do bem, nome ou departamento..."
          class="movimentacoes-filtro-pesquisa"
        >
          <template #prepend><q-icon name="search" /></template>
        </q-input>
      </div>

      <div class="col-auto">
        <q-select
          v-model="tipo"
          outlined
          dense
          clearable
          label="Todos os tipos"
          :options="tipos"
          class="movimentacoes-filtro-tipo"
        />
      </div>

      <div class="col-auto">
        <q-input
          v-model="data"
          outlined
          dense
          clearable
          placeholder="dd/mm/aa"
          class="movimentacoes-filtro-data"
        >
          <template #append><q-icon name="event" class="cursor-pointer" /></template>
        </q-input>
      </div>
    </div>

    <div v-if="filtrosAtivos.length" class="movimentacoes-filtros-ativos">
      <span class="movimentacoes-filtros-ativos-label">Filtros ativos:</span>

      <q-chip
        v-for="filtro in filtrosAtivos"
        :key="filtro.chave"
        removable
        dense
        class="movimentacoes-filtro-ativo-chip"
        @remove="limparFiltro(filtro.chave)"
      >
        {{ filtro.rotulo }}: {{ filtro.valor }}
      </q-chip>

      <q-btn
        flat
        dense
        no-caps
        label="Limpar filtros"
        class="movimentacoes-limpar-filtros"
        @click="limparFiltros"
      />
    </div>

    <MovimentacoesCadastroMovimentos v-model="dialogCadastroMovimentacao" />
  </q-card>
</template>

<script setup>
import { computed, ref } from 'vue'

import MovimentacoesCadastroMovimentos from './MovimentacoesCadastroMovimentos.vue'

const props = defineProps({
  movimentacoes: {
    type: Array,
    default: () => [],
  },
  filtros: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['update:filtros'])

const dialogCadastroMovimentacao = ref(false)

const atualizarFiltro = (chave, valor) => {
  emit('update:filtros', {
    ...props.filtros,
    [chave]: valor || null,
  })
}

const busca = computed({
  get: () => props.filtros.busca,
  set: (valor) => atualizarFiltro('busca', valor),
})

const tipo = computed({
  get: () => props.filtros.tipo,
  set: (valor) => atualizarFiltro('tipo', valor),
})

const data = computed({
  get: () => props.filtros.data,
  set: (valor) => atualizarFiltro('data', valor),
})

const tipos = ['Transferência', 'Devolução', 'Empréstimo', 'Movimentação Interna']

const resumoMovimentacoes = computed(() => ({
  total: props.movimentacoes.length,
  emAberto: props.movimentacoes.filter((movimentacao) => movimentacao.statusClasse === 'aberto').length,
  pendente: props.movimentacoes.filter((movimentacao) => movimentacao.statusClasse === 'pendente').length,
  concluido: props.movimentacoes.filter((movimentacao) => movimentacao.statusClasse === 'concluido').length,
}))

const filtrosAtivos = computed(() => [
  props.filtros.busca && { chave: 'busca', rotulo: 'Busca', valor: props.filtros.busca },
  props.filtros.tipo && { chave: 'tipo', rotulo: 'Tipo', valor: props.filtros.tipo },
  props.filtros.data && { chave: 'data', rotulo: 'Data', valor: props.filtros.data },
].filter(Boolean))

const limparFiltro = (chave) => atualizarFiltro(chave, '')

const limparFiltros = () => {
  emit('update:filtros', { busca: '', tipo: null, data: null })
}

const novaMovimentacao = () => {
  dialogCadastroMovimentacao.value = true
}
</script>
