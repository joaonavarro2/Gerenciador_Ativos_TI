<template>
  <q-card flat bordered class="bens-header escritorios-header">
    <div class="row items-start justify-between q-mb-lg">
      <div>
        <div class="text-h4 text-weight-bold">Escritórios</div>
        <div class="text-subtitle1 text-grey-7 q-mt-xs">
          Gerencie os escritórios e suas informações cadastrais.
        </div>
      </div>

      <div class="row q-gutter-sm">
        <q-btn @click="$emit('novo-escritorio')"  color="positive" icon="add" label="Novo Escritório" no-caps />
      </div>
    </div>

    <div class="row q-col-gutter-md">
      <div class="col-12 col-md-4">
        <div class="field-label">Buscar</div>
        <q-input
          v-model="busca"
          outlined
          dense
          placeholder="Buscar por nome, cidade ou responsável..."
          clearable
          class="historico-busca"
        >
          <template #prepend>
            <q-icon name="search" />
          </template>
        </q-input>
      </div>

      <div class="col-12 col-md-3">
        <div class="field-label">Departamento</div>
        <q-select
          v-model="departamento"
          outlined
          dense
          clearable
          :options="departamentos"
          label="Departamento"
          class="historico-filtro"
        />
      </div>

      <div class="col-12 col-md-3">
        <div class="field-label">Cidade</div>
        <q-select
          v-model="cidade"
          outlined
          dense
          clearable
          :options="cidades"
          label="Cidade"
          class="historico-filtro"
        />
      </div>

      <div class="col-12 col-md-2">
        <div class="field-label">Status</div>
        <q-select
          v-model="status"
          outlined
          dense
          clearable
          :options="statusOptions"
          label="Status"
          class="historico-filtro"
        />
      </div>
    </div>

    <div v-if="possuiFiltros" class="row items-center q-gutter-sm q-mt-lg">
      <div class="text-caption text-grey-7">Filtros aplicados:</div>

      <q-chip v-if="busca" removable color="blue-1" text-color="primary" icon="search" @remove="busca = ''">
        Pesquisa: {{ busca }}
      </q-chip>

      <q-chip v-if="departamento" removable color="blue-1" text-color="primary" icon="location_on" @remove="departamento = null">
        Departamento: {{ departamento }}
      </q-chip>

      <q-chip v-if="cidade" removable color="blue-1" text-color="primary" icon="location_city" @remove="cidade = null">
        Cidade: {{ cidade }}
      </q-chip>

      <q-chip v-if="status" removable color="blue-1" text-color="primary" icon="verified" @remove="status = null">
        Status: {{ status }}
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

const emit = defineEmits(['update:filtros, novo-escritorio'])

const criarFiltro = (chave) => computed({
  get: () => props.filtros[chave],
  set: (valor) => emit('update:filtros', { ...props.filtros, [chave]: valor || null }),
})

const busca = criarFiltro('busca')
const departamento = criarFiltro('departamento')
const cidade = criarFiltro('cidade')
const status = criarFiltro('status')

const departamentos = ['Assessoria da Diretoria',
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
            'Coord.Articulação de Políticas']
const cidades = ['Alagoinhas',
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
  'Vitória da Conquista']
const statusOptions = ['Ativo', 'Inativo', 'Em expansão']

const possuiFiltros = computed(() => Object.values(props.filtros).some(value => value !== null && value !== ''))
</script>

<style scoped>
.bens-header .q-field__label {
  font-weight: 700;
  white-space: normal;
}

.escritorios-header {
  padding: 28px;
}

.field-label {
  font-weight: 700;
  margin-bottom: 6px;
  font-size: 13px;
  color: #334155;
}
</style>
