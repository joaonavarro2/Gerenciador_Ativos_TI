<template>
  <q-dialog
    v-model="aberto"
    persistent
    transition-show="fade"
    transition-hide="fade"
  >
    <q-card class="reparo-dialog-card">
      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar color="green-1" text-color="teal" icon="edit" size="48px" />

          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">Editar Reparo</div>
            <div class="text-subtitle2 text-grey-7">Atualize as informações do serviço em andamento.</div>
          </div>
        </div>

        <q-btn flat round dense icon="close" @click="fechar" />
      </q-card-section>

      <q-separator />

      <q-form class="dialog-form" @submit.prevent="salvarReparo">
        <q-card-section class="q-pt-md dialog-scroll">
          <div class="reparo-secao">
            <div class="titulo-secao row items-center q-mb-md">
              <q-icon name="build_circle" color="positive" size="20px" />
              <div class="q-ml-sm">ATIVO E TIPO DE SERVIÇO</div>
            </div>

            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.ativoId"
                  outlined
                  dense
                  label="ID do Ativo *"
                  :rules="[valorObrigatorio]"
                  hide-bottom-space
                />
              </div>

              <div class="col-12 col-md-6">
                <q-select
                  v-model="form.tipo"
                  outlined
                  dense
                  :options="tiposReparo"
                  label="Tipo de Reparo *"
                  :rules="[valorObrigatorio]"
                  hide-bottom-space
                />
              </div>

              <div class="col-12">
                <q-input
                  v-model="form.problema"
                  outlined
                  dense
                  label="Descrição do Problema *"
                  :rules="[valorObrigatorio]"
                  hide-bottom-space
                />
              </div>

              <div class="col-12 col-md-6">
                <q-select
                  v-model="form.status"
                  outlined
                  dense
                  :options="statusOptions"
                  label="Status *"
                  :rules="[valorObrigatorio]"
                  hide-bottom-space
                />
              </div>

              <div class="col-12">
                <q-input
                  v-model="form.descricao"
                  outlined
                  type="textarea"
                  autogrow
                  label="Detalhes do Reparo"
                />
              </div>
            </div>
          </div>

          <div class="reparo-secao q-mt-xl">
            <div class="titulo-secao row items-center q-mb-md">
              <q-icon name="person" color="positive" size="20px" />
              <div class="q-ml-sm">RESPONSÁVEL TÉCNICO</div>
            </div>

            <div class="col-12">
              <q-input
                v-model="form.tecnico"
                outlined
                dense
                label="Técnico Responsável *"
                :rules="[valorObrigatorio]"
                hide-bottom-space
              />
            </div>
          </div>

          <div class="reparo-secao q-mt-xl">
            <div class="titulo-secao row items-center q-mb-md">
              <q-icon name="event" color="positive" size="20px" />
              <div class="q-ml-sm">CRONOGRAMA</div>
            </div>

            <div class="row q-col-gutter-md">
              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.dataInicio"
                  outlined
                  dense
                  label="Data de Início *"
                  :rules="[valorObrigatorio]"
                  hide-bottom-space
                >
                  <template #append>
                    <q-icon name="event" class="cursor-pointer" />
                  </template>
                </q-input>
              </div>

              <div class="col-12 col-md-6">
                <q-input
                  v-model="form.dataConclusao"
                  outlined
                  dense
                  label="Data de Conclusão"
                >
                  <template #append>
                    <q-icon name="event" class="cursor-pointer" />
                  </template>
                </q-input>
              </div>
            </div>
          </div>

          <div class="reparo-secao q-mt-xl">
            <div class="titulo-secao row items-center q-mb-md">
              <q-icon name="upload_file" color="positive" size="20px" />
              <div class="q-ml-sm">ANEXOS</div>
            </div>

            <input
              ref="inputArquivo"
              type="file"
              class="hidden-file-input"
              multiple
              accept=".pdf,.jpg,.jpeg,.png,.gif,.doc,.docx"
              @change="selecionarArquivos"
            />

            <div class="anexo-upload" @click="abrirSelecionadorArquivos">
              <q-icon name="cloud_upload" color="positive" size="30px" />
              <div class="text-body1 text-weight-medium q-mt-sm">Clique para fazer upload ou arraste arquivos</div>
              <div class="text-grey-7 q-mt-xs">PDF, JPEG, PNG, XLSX até 20MB por arquivo</div>
            </div>
          </div>
        </q-card-section>

        <q-card-actions align="right" class="q-pa-md dialog-actions">
          <q-btn flat no-caps label="Cancelar" @click="fechar" />
          <q-btn
            type="submit"
            color="positive"
            no-caps
            icon="check_circle"
            label="Salvar Alterações"
            :disable="!formValido"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const aberto = defineModel({ type: Boolean, default: false })
const props = defineProps({
  reparo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['salvar'])
const inputArquivo = ref(null)

const tiposReparo = ['Reparo', 'Substituição', 'Instalação']
const statusOptions = ['Pendente', 'Em andamento', 'Concluído']

const form = ref({
  ativoId: '',
  tipo: null,
  problema: '',
  descricao: '',
  status: 'Pendente',
  tecnico: '',
  dataInicio: '',
  dataConclusao: '',
  anexos: [],
})

const formValido = computed(
  () =>
    form.value.ativoId &&
    form.value.tipo &&
    form.value.problema &&
    form.value.status &&
    form.value.tecnico &&
    form.value.dataInicio,
)

watch(
  () => props.reparo,
  (reparo) => {
    if (reparo && Object.keys(reparo).length) {
      form.value = {
        ativoId: reparo.ativoId || '',
        tipo: reparo.tipo || null,
        problema: reparo.problema || '',
        descricao: reparo.descricao || '',
        status: reparo.status || 'Pendente',
        tecnico: reparo.tecnico || '',
        dataInicio: reparo.dataInicio || reparo.data || '',
        dataConclusao: reparo.dataConclusao || '',
        anexos: reparo.anexos || [],
      }
    }
  },
  { immediate: true },
)

function valorObrigatorio(valor) {
  return !!valor || 'Campo obrigatório'
}

function fechar() {
  aberto.value = false
}

function abrirSelecionadorArquivos() {
  inputArquivo.value?.click()
}

function selecionarArquivos(event) {
  const arquivos = Array.from(event.target.files || [])
  form.value.anexos = arquivos
  event.target.value = ''
}

function salvarReparo() {
  if (!formValido.value) return

  emit('salvar', {
    ...props.reparo,
    ativoId: form.value.ativoId,
    tipo: form.value.tipo,
    problema: form.value.problema,
    descricao: form.value.descricao || 'Sem detalhes adicionais.',
    status: form.value.status,
    tecnico: form.value.tecnico,
    dataInicio: form.value.dataInicio,
    dataConclusao: form.value.dataConclusao,
    data: form.value.dataInicio,
    anexos: form.value.anexos,
  })

  fechar()
}
</script>

<style scoped>
.reparo-dialog-card {
  width: min(960px, 92vw);
  max-width: 92vw;
  max-height: 90vh;
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-form {
  display: flex;
  flex-direction: column;
  min-height: 0;
  flex: 1 1 auto;
}

.dialog-scroll {
  overflow-y: auto;
  flex: 1 1 auto;
  min-height: 0;
}

.dialog-actions {
  border-top: 1px solid #e5e7eb;
  background: #fff;
  position: sticky;
  bottom: 0;
}

.reparo-secao {
  border-top: 1px solid #e5e7eb;
  padding-top: 20px;
}

.reparo-secao:first-child {
  border-top: none;
  padding-top: 0;
}

.titulo-secao {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #1f2937;
  gap: 8px;
}

.hidden-file-input {
  display: none;
}

.anexo-upload {
  border: 1px dashed #d1d5db;
  border-radius: 12px;
  background: #fafafa;
  min-height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #374151;
  padding: 22px 18px;
  cursor: pointer;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.anexo-upload:hover {
  border-color: #4caf50;
  background: #f3fbf4;
}
</style>
