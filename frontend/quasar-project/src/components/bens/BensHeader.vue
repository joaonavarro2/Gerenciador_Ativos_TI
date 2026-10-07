<template>
  <q-card flat bordered class="bens-header">
    <!-- =====================================================
     TOPO
====================================================== -->

    <div class="row items-start justify-between q-mb-lg">
      <div>
        <div class="text-h4 text-weight-bold">Inventário de Bens</div>

        <div class="text-subtitle1 text-grey-7 q-mt-xs">
          Registro completo de todos os bens e equipamentos cadastrados.
        </div>
      </div>

      <div class="row q-gutter-sm">
        <!-- Exportar -->
        <q-btn
          @click="exportarBem"
          outline
          color="grey-8"
          icon="download"
          label="Exportar"
          no-caps
        />

        <!--
      IMPORTANTE:
      Envia os filtros atuais para o componente
      de exportação.
    -->
        <ExportarRelatorio v-model="dialogExportar" :filtros="filtros" :itens="props.itens" />

        <!-- Novo bem -->
        <q-btn @click="cadastrarBem" color="positive" icon="add" label="Novo Bem" no-caps />

        <BemCadastro
          v-model="dialogCadastro"
          :opcoes="props.opcoes"
          @salvo="emit('bem-salvo', $event)"
        />
      </div>
    </div>

    <!-- =====================================================
     FILTROS
====================================================== -->

    <div class="row q-col-gutter-md">
      <!-- ===================================================
       PESQUISA
  ==================================================== -->

      <div class="col">
        <q-input
          v-model="filtros.busca"
          outlined
          dense
          placeholder="Buscar por ID, descrição, número de série..."
          clearable
        >
          <template #prepend>
            <q-icon name="search" />
          </template>
        </q-input>
      </div>

      <!-- ===================================================
       CATEGORIA
  ==================================================== -->

      <div class="col-auto">
        <q-select
          v-model="filtros.categoria"
          multiple
          outlined
          dense
          clearable
          style="width: 180px"
          label="Tipo de Bem"
          :options="props.opcoes.categorias"
        />
      </div>

      <!-- ===================================================
       STATUS
  ==================================================== -->

      <div class="col-auto">
        <q-select
          v-model="filtros.status"
          multiple
          outlined
          dense
          clearable
          style="width: 170px"
          label="Status"
          :options="statusDisponiveis"
        />
      </div>

      <!-- ===================================================
       DEPARTAMENTO
  ==================================================== -->

      <div class="col-auto">
        <q-select
          v-model="filtros.departamento"
          outlined
          dense
          clearable
          style="width: 210px"
          label="Departamento"
          :options="departamentosDisponiveis"
        />
      </div>
    </div>

    <!-- =====================================================
     FILTROS ATIVOS
====================================================== -->

    <div v-if="possuiFiltros" class="row items-center q-gutter-sm q-mt-md">
      <div class="text-caption text-grey-7">Filtros aplicados:</div>

      <!-- Pesquisa -->
      <q-chip
        v-if="filtros.busca"
        removable
        color="blue-1"
        text-color="primary"
        icon="search"
        @remove="filtros.busca = ''"
      >
        Pesquisa: {{ filtros.busca }}
      </q-chip>

      <!-- Categoria -->
      <q-chip
        v-for="categoria in filtros.categoria"
        :key="categoria"
        removable
        color="blue-1"
        text-color="primary"
        icon="category"
        @remove="filtros.categoria = filtros.categoria.filter((item) => item !== categoria)"
      >
        Categoria: {{ categoria }}
      </q-chip>

      <!-- Status -->
      <q-chip
        v-for="status in filtros.status"
        :key="status"
        removable
        color="blue-1"
        text-color="primary"
        icon="verified"
        @remove="filtros.status = filtros.status.filter((item) => item !== status)"
      >
        Status: {{ status }}
      </q-chip>

      <!-- Departamento -->
      <q-chip
        v-if="filtros.departamento"
        removable
        color="blue-1"
        text-color="primary"
        icon="apartment"
        @remove="filtros.departamento = ''"
      >
        Departamento: {{ filtros.departamento }}
      </q-chip>
    </div>
  </q-card>
</template>

<script setup>
import { computed, ref } from 'vue'
import BemCadastro from './cadastro/Cadastro.vue'
import ExportarRelatorio from '@/components/common/ExportarRelatorio.vue'
const dialogCadastro = ref(false)
const dialogExportar = ref(false)

const props = defineProps({
  itens: {
    type: Array,
    default: () => [],
  },
  opcoes: {
    type: Object,
    default: () => ({
      escritorios: [],
      departamentos: [],
      pessoas: [],
      categorias: [],
      status: [],
    }),
  },
  filtros: {
    type: Object,
    default: () => ({ busca: '', categoria: [], status: [], departamento: '' }),
  },
})

const emit = defineEmits(['update:filtros', 'bem-salvo'])
const filtros = props.filtros
const statusDisponiveis = computed(() => [
  ...new Set([...(props.opcoes.status || []), 'Ativo', 'Em Manutenção', 'Inativo', 'Descartado']),
])
const departamentosDisponiveis = computed(() => [...new Set([
  'Assessoria da Diretoria',
  'Controle Interno',
  'Comitê Técnico Ambiental',
  'Diretoria Geral',
  'Assessoria de Comunicação',
  'Assessoria Jurídica',
  'Dep.Recursos Humanos',
  'Dep.Administrativo',
  'Dep.Convênios e Contratos',
  'Dep.Financeiro',
  'Dep.Licitações',
  'Dep.Capacitação',
  'Dep.Engenharia',
  'Coord.Água para Todos',
  'Coord.Pró-Semiárido',
  'Coord.Bahia Produtiva',
  'Coord.Projetos Especiais',
  'Coord.Articulação de Políticas',
  ...(props.opcoes.departamentos || []).map((departamento) => departamento.nome),
])])

function cadastrarBem() {
  dialogCadastro.value = true
}

function exportarBem() {
  dialogExportar.value = true
}

/* ======================================================
   FILTROS
====================================================== */

/* ======================================================
   VERIFICA SE EXISTE ALGUM FILTRO
====================================================== */

const possuiFiltros = computed(() => {
  return Boolean(
    (filtros.busca || '').trim() ||
    filtros.categoria.length ||
    filtros.status.length ||
    filtros.departamento,
  )
})
</script>
