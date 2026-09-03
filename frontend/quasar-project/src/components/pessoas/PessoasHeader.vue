<template>
  <q-card flat bordered class="bens-header pessoas-header">
    <div class="row items-start justify-between q-mb-lg">
      <div>
        <div class="text-h4 text-weight-bold">Pessoas</div>
        <div class="text-subtitle1 text-grey-7 q-mt-xs">
          Gerencie os colaboradores responsáveis pelos bens patrimoniais da organização.
        </div>
      </div>

      <div class="row q-gutter-sm">
        <q-btn
          color="positive"
          icon="add"
          label="Nova Pessoa"
          no-caps
          class="pessoas-btn-nova"
          @click="novaPessoa"
        />
      </div>
    </div>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-5">
        <div class="field-label">Buscar</div>
        <q-input
          v-model="busca"
          outlined
          dense
          clearable
          class="pessoas-busca"
          placeholder="Buscar por nome, matrícula, CPF ou e-mail..."
        >
          <template #prepend>
            <q-icon name="search" />
          </template>
        </q-input>
      </div>

      <div class="col-12 col-md-2">
        <div class="field-label">Escritório</div>
        <q-select
          v-model="escritorio"
          outlined
          dense
          clearable
          class="pessoas-select"
          :options="opcoesEscritorios"
          label="Todos os Escritórios"
          behavior="menu"
        />
      </div>

      <div class="col-12 col-md-3">
        <div class="field-label">Departamento</div>
        <q-select
          v-model="departamento"
          outlined
          dense
          clearable
          class="pessoas-select"
          :options="opcoesDepartamentos"
          label="Todos os Departamentos"
          behavior="menu"
        />
      </div>

      <div class="col-12 col-md-2">
        <div class="field-label">Status</div>
        <q-select
          v-model="status"
          outlined
          dense
          clearable
          class="pessoas-select"
          :options="opcoesStatus"
          label="Status"
          behavior="menu"
        />
      </div>
    </div>

    <div v-if="possuiFiltros" class="row items-center q-gutter-sm q-mt-lg">
      <div class="text-caption text-grey-7">Filtros aplicados:</div>

      <q-chip
        v-if="props.filtros.busca"
        removable
        color="blue-1"
        text-color="primary"
        icon="search"
        @remove="busca = ''"
      >
        Pesquisa: {{ props.filtros.busca }}
      </q-chip>

      <q-chip
        v-if="props.filtros.escritorio"
        removable
        color="blue-1"
        text-color="primary"
        icon="location_city"
        @remove="escritorio = null"
      >
        Escritório: {{ props.filtros.escritorio }}
      </q-chip>

      <q-chip
        v-if="props.filtros.departamento"
        removable
        color="blue-1"
        text-color="primary"
        icon="apartment"
        @remove="departamento = null"
      >
        Departamento: {{ props.filtros.departamento }}
      </q-chip>

      <q-chip
        v-if="props.filtros.status"
        removable
        color="blue-1"
        text-color="primary"
        icon="verified"
        @remove="status = null"
      >
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
    required: true
  }
})

const emit = defineEmits(['update:filtros', 'nova-pessoa'])

const opcoesEscritorios = [
  'Alagoinhas',
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
  'Vitória da Conquista'
]

const opcoesDepartamentos = [
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
            'Coord.Articulação de Políticas'
]

const opcoesStatus = ['Status', 'Ativo', 'Inativo']

const busca = computed({
  get: () => props.filtros.busca || '',
  set: (value) => emit('update:filtros', { ...props.filtros, busca: value || '' })
})

const escritorio = computed({
  get: () => props.filtros.escritorio || null,
  set: (value) => emit('update:filtros', { ...props.filtros, escritorio: value || null })
})

const departamento = computed({
  get: () => props.filtros.departamento || null,
  set: (value) => emit('update:filtros', { ...props.filtros, departamento: value || null })
})

const status = computed({
  get: () => props.filtros.status || null,
  set: (value) => emit('update:filtros', { ...props.filtros, status: value || null })
})

const possuiFiltros = computed(() =>
  Object.values(props.filtros).some(
    (value) => value !== null && value !== '' && value !== 'Todos os Escritórios' && value !== 'Todos os Departamentos' && value !== 'Status'
  )
)

function novaPessoa() {
  emit('nova-pessoa')
}
</script>
