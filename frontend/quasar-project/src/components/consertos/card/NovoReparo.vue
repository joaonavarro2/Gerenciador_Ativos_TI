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
          <q-avatar color="green-1" text-color="teal" icon="build_circle" size="48px" />

          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">Novo Reparo</div>
            <div class="text-subtitle2 text-grey-7">Registre um novo serviço de manutenção ou reparo técnico.</div>
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
                <q-select
                  v-model="form.itemId"
                  :options="bens"
                  option-label="rotulo"
                  option-value="id"
                  emit-value
                  map-options
                  use-input
                  input-debounce="200"
                  @filter="filtrarBens"
                  outlined
                  dense
                  label="Ativo *"
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
                  placeholder="Selecione..."
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
                  placeholder="Descreva o problema identificado no ativo..."
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
                  placeholder="Selecione o status"
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
                  placeholder="Detalhe o procedimento técnico que será realizado..."
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
              <q-select
                v-model="form.tecnico"
                :options="tecnicosFiltrados"
                use-input
                input-debounce="0"
                @filter="filtrarTecnicos"
                outlined
                dense
                label="Técnico Responsável *"
                placeholder="Digite para buscar um usuário"
                :rules="[valorObrigatorio]"
                hide-bottom-space
              >
                <template #no-option>
                  <q-item>
                    <q-item-section class="text-grey-7">
                      {{ todosTecnicos.length ? 'Nenhum nome encontrado.' : 'Nenhum usuário ativo cadastrado.' }}
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
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
                  type="date"
                  label="Data de Início *"
                  placeholder="dd/mm/aaaa"
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
                  type="date"
                  label="Data de Conclusão"
                  placeholder="dd/mm/aaaa"
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
              <div v-if="form.anexos.length" class="row q-gutter-xs q-mt-sm">
                <q-chip v-for="arquivo in form.anexos" :key="arquivo.name" removable @click.stop @remove="removerAnexo(arquivo)">{{ arquivo.name }}</q-chip>
              </div>
              <div class="text-body1 text-weight-medium q-mt-sm">Clique para fazer upload ou arraste arquivos</div>
              <div class="text-grey-7 q-mt-xs">PDF, JPG, PNG, GIF, DOC ou DOCX — até 20 MB por arquivo</div>
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
            label="Salvar Reparo"
            :loading="salvando"
            :disable="!formValido || salvando"
          />
        </q-card-actions>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { api } from '@/boot/axios.js'

const aberto = defineModel({ type: Boolean, default: false })
defineProps({ salvando: Boolean })
const emit = defineEmits(['salvar'])
const inputArquivo = ref(null)
const todosBens = ref([])
const bens = ref([])
const todosTecnicos = ref([])
const tecnicosFiltrados = ref([])

const tiposReparo = ['Reparo', 'Substituição', 'Instalação']
const statusOptions = ['Pendente', 'Em andamento', 'Concluído']

const form = ref({
  itemId: null,
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
    form.value.itemId &&
    form.value.tipo &&
    form.value.problema &&
    form.value.status &&
    form.value.tecnico &&
    form.value.dataInicio,
)

function valorObrigatorio(valor) {
  return !!valor || 'Campo obrigatório'
}

function resetarFormulario() {
  form.value = {
    itemId: null,
    tipo: null,
    problema: '',
    descricao: '',
    status: 'Pendente',
    tecnico: '',
    dataInicio: '',
    dataConclusao: '',
    anexos: [],
  }
}

function fechar() {
  aberto.value = false
  resetarFormulario()
}

function abrirSelecionadorArquivos() {
  inputArquivo.value?.click()
}

function selecionarArquivos(event) {
  const arquivos = Array.from(event.target.files || [])
  form.value.anexos.push(...arquivos.filter((arquivo) => arquivo.size <= 20 * 1024 * 1024))
  event.target.value = ''
}

function removerAnexo(arquivo) {
  form.value.anexos = form.value.anexos.filter((item) => item !== arquivo)
}

function filtrarBens(valor, atualizar) {
  atualizar(() => {
    const termo = (valor || '').toLocaleLowerCase('pt-BR')
    bens.value = todosBens.value.filter((bem) => bem.rotulo.toLocaleLowerCase('pt-BR').includes(termo))
  })
}

function normalizarTexto(valor) {
  return String(valor || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('pt-BR')
}

function filtrarTecnicos(valor, atualizar) {
  atualizar(() => {
    const termo = normalizarTexto(valor)
    tecnicosFiltrados.value = todosTecnicos.value.filter((nome) => normalizarTexto(nome).includes(termo))
  })
}

function salvarReparo() {
  if (!formValido.value) return

  emit('salvar', {
    itemId: form.value.itemId,
    tipo: form.value.tipo,
    descricaoProblema: form.value.problema,
    descricaoSolucao: form.value.descricao || null,
    status: form.value.status,
    tecnico: form.value.tecnico,
    dataEntrada: `${form.value.dataInicio}T00:00:00`,
    dataInicio: form.value.dataInicio,
    dataSaida: form.value.dataConclusao ? `${form.value.dataConclusao}T00:00:00` : null,
    anexos: form.value.anexos,
    escritorio: 'Não informado',
    departamento: 'Não informado',
  })

}

onMounted(async () => {
  try {
    const { data } = await api.get('/bens')
    todosBens.value = data.map((bem) => ({ ...bem, rotulo: `${bem.codigo} · ${bem.nome}` }))
    bens.value = todosBens.value
  } catch { /* A página informa falhas ao carregar os registros. */ }

  try {
    const { data } = await api.get('/consertos/tecnicos')
    todosTecnicos.value = data
    tecnicosFiltrados.value = data
  } catch { /* A lista fica vazia se a API não estiver disponível. */ }
})
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
