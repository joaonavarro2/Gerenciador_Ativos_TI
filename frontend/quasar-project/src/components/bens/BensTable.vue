<template>
  <q-card flat bordered class="bens-table">
    <div class="table-header">
      <div class="table-title">
        <div class="table-title-main">Bens Cadastrados</div>
        <div class="table-title-sub">{{ bens.length }} registro(s)</div>
      </div>
      <div class="table-last-update">
        <q-icon name="inventory_2" size="16px" /> Inventário atualizado
      </div>
    </div>

    <q-table
      flat
      :rows="bens"
      :columns="columns"
      :loading="loading"
      row-key="id"
      separator="horizontal"
    >
      <template #body="tableProps">
        <q-tr :props="tableProps">
          <q-td key="id" :props="tableProps"
            ><q-badge class="badge-id">{{ tableProps.row.codigo }}</q-badge></q-td
          >
          <q-td key="descricao" :props="tableProps">
            <div class="descricao-cell">
              <q-avatar
                rounded
                :color="tableProps.row.avatarColor"
                :text-color="tableProps.row.iconColor"
                size="38px"
              >
                <q-icon :name="tableProps.row.icon" size="18px" />
              </q-avatar>
              <div class="descricao-title">{{ tableProps.row.descricao }}</div>
            </div>
          </q-td>
          <q-td key="categoria" :props="tableProps"
            ><q-chip dense square class="categoria-chip">{{
              tableProps.row.categoria
            }}</q-chip></q-td
          >
          <q-td key="marca" :props="tableProps">{{ tableProps.row.marca }}</q-td>
          <q-td key="departamento" :props="tableProps">{{ tableProps.row.departamento }}</q-td>
          <q-td key="responsavel" :props="tableProps">
            <div class="responsavel-cell">
              <q-avatar size="28px" color="blue-1" text-color="primary"
                ><q-icon name="person" size="15px" /></q-avatar
              >{{ tableProps.row.responsavel || 'Não atribuído' }}
            </div>
          </q-td>
          <q-td key="status" :props="tableProps"
            ><q-chip dense :class="statusClass(tableProps.row.status)">{{
              tableProps.row.status
            }}</q-chip></q-td
          >
          <q-td key="acoes" :props="tableProps" class="text-center">
            <q-btn
              flat
              round
              dense
              class="acao-btn"
              aria-label="Visualizar bem"
              @click="visualizarBem(tableProps.row)"
              ><q-icon name="visibility" size="18px" /><q-tooltip>Visualizar bem</q-tooltip></q-btn
            >
            <q-btn
              flat
              round
              dense
              class="acao-btn"
              aria-label="Movimentar bem"
              @click="movimentarBem(tableProps.row.id)"
              ><q-icon name="sync_alt" size="18px" /><q-tooltip>Movimentar bem</q-tooltip></q-btn
            >
            <q-btn
              flat
              round
              dense
              class="acao-btn"
              aria-label="Editar bem"
              @click="editarBem(tableProps.row)"
              ><q-icon name="edit" size="18px" /><q-tooltip>Editar bem</q-tooltip></q-btn
            >
            <q-btn
              flat
              round
              dense
              class="acao-btn"
              aria-label="Excluir bem"
              @click="deletarBem(tableProps.row)"
              ><q-icon name="delete" size="18px" /><q-tooltip>Excluir bem</q-tooltip></q-btn
            >
          </q-td>
        </q-tr>
      </template>
      <template #no-data>
        <div class="full-width row flex-center text-grey-7 q-pa-lg">
          {{ loading ? 'Carregando inventário...' : 'Nenhum bem encontrado.' }}
        </div>
      </template>
    </q-table>

    <div class="table-footer">
      <div class="footer-left">Exibindo {{ bens.length }} bens</div>
      <div class="footer-right">
        <q-chip dense class="status-ativo">Ativo: {{ totalAtivos }}</q-chip>
        <q-chip dense class="status-manutencao">Em Manutenção: {{ totalManutencao }}</q-chip>
        <q-chip dense class="status-inativo">Inativo: {{ totalInativos }}</q-chip>
        <q-chip dense class="status-descartado">Descartado: {{ totalDescartados }}</q-chip>
      </div>
    </div>
  </q-card>

  <BemDetailsDialog v-model="dialogDetalhes" :bem="bemSelecionado" />
  <MovimentacaoRegister
    v-model="dialogMovimentacao"
    :bem-id="bemMovimentacao"
    :opcoes="opcoes"
    @movimentado="emit('bens-atualizados')"
  />
  <DeleteDialog v-model="dialogDelete" :bem="bemExcluir" @remover-bem="emit('bens-atualizados')" />
  <BemCadastroDialog
    v-model="dialogCadastro"
    :bem="bemEditando"
    :opcoes="opcoes"
    @salvo="emit('bens-atualizados', $event)"
  />
</template>

<script setup>
import { computed, ref } from 'vue'
import BemDetailsDialog from './cards/BensDialogs.vue'
import MovimentacaoRegister from './cards/MovimentacaoRegister.vue'
import DeleteDialog from './cards/DeleteDialog.vue'
import BemCadastroDialog from './cadastro/Cadastro.vue'

const props = defineProps({
  bens: { type: Array, default: () => [] },
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
  loading: { type: Boolean, default: false },
})
const emit = defineEmits(['bens-atualizados'])
const bens = computed(() => props.bens)
const columns = [
  { name: 'id', label: 'CÓDIGO', field: 'codigo', align: 'left' },
  { name: 'descricao', label: 'DESCRIÇÃO', field: 'descricao', align: 'left' },
  { name: 'categoria', label: 'CATEGORIA', field: 'categoria', align: 'left' },
  { name: 'marca', label: 'MARCA / MODELO', field: 'marca', align: 'left' },
  { name: 'departamento', label: 'DEPARTAMENTO', field: 'departamento', align: 'left' },
  { name: 'responsavel', label: 'RESPONSÁVEL', field: 'responsavel', align: 'left' },
  { name: 'status', label: 'STATUS', field: 'status', align: 'left' },
  { name: 'acoes', label: 'AÇÕES', align: 'center' },
]
const dialogDetalhes = ref(false)
const bemSelecionado = ref(null)
const dialogMovimentacao = ref(false)
const bemMovimentacao = ref(null)
const dialogDelete = ref(false)
const bemExcluir = ref(null)
const dialogCadastro = ref(false)
const bemEditando = ref(null)
const totalAtivos = computed(() => bens.value.filter((bem) => bem.status === 'Ativo').length)
const totalManutencao = computed(
  () => bens.value.filter((bem) => bem.status === 'Em Manutenção').length,
)
const totalInativos = computed(() => bens.value.filter((bem) => bem.status === 'Inativo').length)
const totalDescartados = computed(
  () => bens.value.filter((bem) => bem.status === 'Descartado').length,
)

function statusClass(status) {
  const classes = {
    Ativo: 'status-chip status-ativo',
    'Em Manutenção': 'status-chip status-manutencao',
    Inativo: 'status-chip status-inativo',
    Descartado: 'status-chip status-descartado',
  }
  return classes[status] || 'status-chip'
}

function visualizarBem(bem) {
  bemSelecionado.value = bem
  dialogDetalhes.value = true
}

function editarBem(bem) {
  bemEditando.value = { ...bem }
  dialogCadastro.value = true
}

function movimentarBem(id) {
  bemMovimentacao.value = id
  dialogMovimentacao.value = true
}

function deletarBem(bem) {
  bemExcluir.value = bem
  dialogDelete.value = true
}
</script>
