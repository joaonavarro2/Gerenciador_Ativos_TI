<template>
  <q-dialog v-model="aberto" persistent class="cadastro-movimentacao-modal">
    <q-card class="cadastro-movimentacao-dialog">
      <q-card-section class="cadastro-movimentacao-header">
        <div>
          <div class="cadastro-movimentacao-title">Registrar Movimentação</div>
          <div class="cadastro-movimentacao-subtitle">
            Preencha os dados do bem e da movimentação a ser registrada.
          </div>
        </div>

        <q-btn v-close-popup flat round dense icon="close" class="cadastro-movimentacao-close" />
      </q-card-section>

      <q-form @submit="registrarMovimentacao">
        <q-card-section class="cadastro-movimentacao-content">
          <div class="cadastro-movimentacao-grid">
            <q-input
              v-model="form.patrimonio"
              outlined
              dense
              label="ID do Bem *"
              placeholder="Ex: BA-042891"
              class="cadastro-movimentacao-field cadastro-movimentacao-field-full"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            >
              <template #append>
                <q-icon name="qr_code_scanner" />
              </template>
            </q-input>

            <q-input
              v-model="form.nomeBem"
              outlined
              dense
              label="Nome do Bem"
              placeholder="Ex: Notebook Dell Latitude"
              class="cadastro-movimentacao-field"
            />

            <q-select
              v-model="form.tipo"
              outlined
              dense
              label="Tipo de Movimentação *"
              placeholder="Selecione..."
              :options="tiposMovimentacao"
              class="cadastro-movimentacao-field"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            />

            <q-input
              v-model="form.data"
              outlined
              dense
              label="Data da Movimentação *"
              placeholder="dd/mm/aaaa"
              class="cadastro-movimentacao-field"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            >
              <template #append>
                <q-icon name="event" />
              </template>
            </q-input>

            <div class="cadastro-movimentacao-field cadastro-movimentacao-field-empty" />

            <q-select
              v-model="form.escritorioOrigem"
              outlined
              dense
              label="Escritório de Origem *"
              placeholder="Selecione..."
              :options="escritorios"
              class="cadastro-movimentacao-field"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            />

            <q-select
              v-model="form.escritorioDestino"
              outlined
              dense
              label="Escritório de Destino *"
              placeholder="Selecione..."
              :options="escritorios"
              class="cadastro-movimentacao-field"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            />

            <q-select
              v-model="form.departamentoOrigem"
              outlined
              dense
              label="Departamento de Origem *"
              placeholder="Selecione..."
              :options="departamentos"
              class="cadastro-movimentacao-field"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            />

            <q-select
              v-model="form.departamentoDestino"
              outlined
              dense
              label="Departamento de Destino *"
              placeholder="Selecione..."
              :options="departamentos"
              class="cadastro-movimentacao-field"
              :rules="[valorObrigatorio]"
              hide-bottom-space
            />

            <q-select
              v-model="form.responsavelEntrega"
              outlined
              dense
              label="Responsável pela Entrega"
              placeholder="Selecione..."
              :options="responsaveis"
              class="cadastro-movimentacao-field"
            />

            <q-select
              v-model="form.responsavelRecebimento"
              outlined
              dense
              label="Responsável pelo Recebimento"
              placeholder="Selecione..."
              :options="responsaveis"
              class="cadastro-movimentacao-field"
            />

            <q-input
              v-model="form.justificativa"
              outlined
              type="textarea"
              label="Justificativa / Motivo"
              placeholder="Descreva o motivo desta movimentação..."
              class="cadastro-movimentacao-field cadastro-movimentacao-field-full"
              input-style="min-height: 70px"
            />
          </div>
        </q-card-section>

        <q-card-actions align="right" class="cadastro-movimentacao-footer">
          <q-btn v-close-popup outline no-caps label="Cancelar" class="cadastro-movimentacao-cancelar" />
          <q-btn
            type="submit"
            unelevated
            no-caps
            icon="check_circle_outline"
            label="Registrar Movimentação"
            class="cadastro-movimentacao-salvar"
            :disable="!formValido"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'

const aberto = defineModel({ type: Boolean, default: false })

const emit = defineEmits(['registrar'])

const form = ref({
  patrimonio: '',
  nomeBem: '',
  tipo: null,
  data: '',
  escritorioOrigem: null,
  escritorioDestino: null,
  departamentoOrigem: null,
  departamentoDestino: null,
  responsavelEntrega: null,
  responsavelRecebimento: null,
  justificativa: '',
})

const tiposMovimentacao = ['Transferência', 'Devolução', 'Empréstimo', 'Movimentação Interna']

const escritorios = [
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
  'Vitória da Conquista',
]

const departamentos = [
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
]

const responsaveis = ['Fernanda Rocha', 'João Matos', 'Marcos Silva']

const valorObrigatorio = (valor) => !!valor || 'Campo obrigatório'

const formValido = computed(() =>
  form.value.patrimonio &&
  form.value.tipo &&
  form.value.data &&
  form.value.escritorioOrigem &&
  form.value.escritorioDestino &&
  form.value.departamentoOrigem &&
  form.value.departamentoDestino,
)

function registrarMovimentacao() {
  /*
   * BACKEND — SPRING BOOT
   *
   * const response = await api.post('/movimentacoes', form.value)
   * emit('registrar', response.data)
   * aberto.value = false
   *
   * Por enquanto, o componente envia os dados preenchidos ao componente pai.
   */
  emit('registrar', { ...form.value })
  aberto.value = false
}
</script>
