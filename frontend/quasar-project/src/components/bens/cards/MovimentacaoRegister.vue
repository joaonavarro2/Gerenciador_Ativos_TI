<template>
  <q-dialog
    :model-value="modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card
      class="mov-dialog"
      style="
        width: 900px;
        max-width: 95vw;
        max-height: 90vh;
        background: white;
        border-radius: 10px;
      "
    >
      <div class="mov-header">
        <div>
          <div class="mov-title">Registrar Movimentação</div>
          <div class="mov-subtitle">
            Bem:
            <span class="mov-patrimonio">{{
              bemSelecionado.patrimonio || bemSelecionado.codigo || '—'
            }}</span>
            - {{ bemSelecionado.nome || 'Carregando bem...' }}
          </div>
        </div>
        <q-btn
          flat
          round
          dense
          icon="close"
          class="dialog-close"
          @click="emit('update:modelValue', false)"
        />
      </div>

      <q-separator />

      <q-form @submit.prevent="registrarMovimentacao">
        <div class="mov-content">
          <div class="row q-col-gutter-lg">
            <div class="col-12 col-md-6">
              <q-select
                v-model="movimentacao.tipo"
                outlined
                :options="tiposMovimentacao"
                label="Tipo de Movimentação *"
                :rules="[obrigatorio]"
              />
            </div>
            <div class="col-12 col-md-6">
              <q-input
                v-model="movimentacao.data"
                outlined
                mask="##/##/####"
                label="Data da Movimentação *"
                :rules="[obrigatorio]"
              >
                <template #append>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                      <q-date v-model="movimentacao.data" mask="DD/MM/YYYY">
                        <div class="row justify-end q-pa-sm">
                          <q-btn v-close-popup flat label="Fechar" />
                        </div>
                      </q-date>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>
            </div>
            <div class="col-12 col-md-6">
              <q-input
                v-model="departamentoOrigem"
                outlined
                readonly
                label="Departamento de Origem"
              />
            </div>
            <div class="col-12 col-md-6">
              <q-input v-model="escritorioOrigem" outlined readonly label="Escritório de Origem" />
            </div>
            <div class="col-12 col-md-6">
              <q-select
                v-model="movimentacao.departamentoDestinoId"
                outlined
                :options="departamentos"
                option-label="nome"
                option-value="id"
                emit-value
                map-options
                label="Departamento de Destino *"
                :rules="[obrigatorio]"
              />
            </div>
            <div class="col-12 col-md-6">
              <q-select
                v-model="movimentacao.escritorioDestinoId"
                outlined
                :options="escritorios"
                option-label="nome"
                option-value="id"
                emit-value
                map-options
                label="Escritório de Destino *"
                :rules="[obrigatorio]"
              />
            </div>
            <div class="col-12">
              <q-select
                v-model="movimentacao.responsavelDestinoId"
                outlined
                clearable
                :options="responsaveisDestino"
                option-label="nome"
                option-value="id"
                emit-value
                map-options
                label="Responsável pelo Recebimento"
              />
            </div>
            <div class="col-12">
              <q-input
                v-model="movimentacao.justificativa"
                outlined
                autogrow
                type="textarea"
                label="Justificativa / Motivo"
              />
            </div>
          </div>
        </div>

        <q-separator />
        <div class="mov-footer">
          <q-btn
            outline
            color="grey-7"
            label="Cancelar"
            @click="emit('update:modelValue', false)"
          />
          <q-btn
            type="submit"
            color="positive"
            icon="check_circle"
            label="Registrar Movimentação"
            :loading="salvando"
          />
        </div>
      </q-form>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useQuasar } from 'quasar'
import { api } from '@/boot/axios'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  bemId: { type: Number, default: null },
  opcoes: {
    type: Object,
    default: () => ({ escritorios: [], departamentos: [], pessoas: [] }),
  },
})

const emit = defineEmits(['update:modelValue', 'movimentado'])
const $q = useQuasar()
const tiposMovimentacao = ['Transferência', 'Empréstimo', 'Devolução']
const bemSelecionado = ref({})
const departamentoOrigem = ref('')
const escritorioOrigem = ref('')
const salvando = ref(false)
const movimentacao = ref({
  tipo: null,
  data: '',
  departamentoDestinoId: null,
  escritorioDestinoId: null,
  responsavelDestinoId: null,
  justificativa: '',
})
const departamentos = computed(() => props.opcoes.departamentos || [])
const escritorios = computed(() => props.opcoes.escritorios || [])
const responsaveisDestino = computed(() => {
  const departamentoId = movimentacao.value.departamentoDestinoId
  return (props.opcoes.pessoas || []).filter(
    (pessoa) => !departamentoId || pessoa.departamentoId === departamentoId,
  )
})

function obrigatorio(valor) {
  return (valor !== null && valor !== undefined && valor !== '') || 'Preencha este campo.'
}

function dataApi(valor) {
  const [dia, mes, ano] = valor.split('/')
  return `${ano}-${mes}-${dia}T12:00:00`
}

async function carregarBem() {
  if (!props.modelValue || !props.bemId) return
  try {
    const { data } = await api.get(`/bens/${props.bemId}`)
    bemSelecionado.value = data
    departamentoOrigem.value = data.departamento || ''
    escritorioOrigem.value = data.escritorio || ''
    movimentacao.value = {
      tipo: null,
      data: new Date().toLocaleDateString('pt-BR'),
      departamentoDestinoId: data.departamentoId,
      escritorioDestinoId: data.escritorioId,
      responsavelDestinoId: data.pessoaId,
      justificativa: '',
    }
  } catch (error) {
    $q.notify({
      type: 'negative',
      message: error.response?.data?.mensagem || 'Não foi possível carregar o bem.',
    })
  }
}

async function registrarMovimentacao() {
  if (
    !props.bemId ||
    !movimentacao.value.tipo ||
    !movimentacao.value.data ||
    !movimentacao.value.departamentoDestinoId ||
    !movimentacao.value.escritorioDestinoId
  ) {
    $q.notify({ type: 'negative', message: 'Preencha o tipo, a data e o destino da movimentação.' })
    return
  }

  salvando.value = true
  try {
    await api.post(`/bens/${props.bemId}/movimentacoes`, {
      tipo: movimentacao.value.tipo,
      data: dataApi(movimentacao.value.data),
      escritorioDestinoId: movimentacao.value.escritorioDestinoId,
      departamentoDestinoId: movimentacao.value.departamentoDestinoId,
      responsavelDestinoId: movimentacao.value.responsavelDestinoId || null,
      justificativa: movimentacao.value.justificativa || null,
    })
    $q.notify({ type: 'positive', message: 'Movimentação registrada com sucesso.' })
    emit('movimentado')
    emit('update:modelValue', false)
  } catch (error) {
    const resposta = error.response?.data
    const mensagem =
      resposta?.mensagem ||
      (resposta &&
        Object.entries(resposta)
          .map(([campo, detalhe]) => `${campo}: ${detalhe}`)
          .join(' ')) ||
      (error.response?.status === 401
        ? 'Sua sessão expirou. Entre novamente para registrar a movimentação.'
        : 'Não foi possível registrar a movimentação.')
    $q.notify({
      type: 'negative',
      message: mensagem,
    })
  } finally {
    salvando.value = false
  }
}

watch(() => [props.modelValue, props.bemId], carregarBem, { immediate: true })
</script>
