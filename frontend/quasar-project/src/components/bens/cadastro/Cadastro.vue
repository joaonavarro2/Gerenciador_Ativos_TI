<!-- eslint-disable vue/multi-word-component-names -->

<template>
  <q-dialog
    :model-value="props.modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card
      class="cadastro-modal"
      style="width: 900px; max-width: 95vw; border-radius: 18px; overflow: hidden"
    >
      <!-- =====================================================
           CABEÇALHO
      ====================================================== -->

      <q-card-section class="row items-center justify-between q-pb-sm">
        <div class="row items-center">
          <q-avatar
            color="green-1"
            text-color="teal"
            :icon="modoEdicao ? 'edit' : 'inventory_2'"
            size="48px"
          />

          <div class="q-ml-md">
            <div class="text-h5 text-weight-bold">
              {{ modoEdicao ? 'Editar Bem' : 'Cadastrar Novo Bem' }}
            </div>

            <div class="text-grey-7">
              {{
                modoEdicao
                  ? 'Altere as informações do bem selecionado.'
                  : 'Registre um novo bem organizacional no sistema de inventário.'
              }}
            </div>
          </div>
        </div>

        <q-btn flat round dense icon="close" @click="fecharFormulario" />
      </q-card-section>

      <q-separator />

      <!-- =====================================================
           CONTEÚDO
      ====================================================== -->

      <q-card-section class="scroll formulario-area">
        <!-- =====================================================
             IDENTIFICAÇÃO
        ====================================================== -->

        <div class="titulo-secao">IDENTIFICAÇÃO BÁSICA</div>

        <div class="row q-col-gutter-md">
          <div class="col-12 col-md-6">
            <q-input
              outlined
              dense
              v-model="form.nome"
              label="Nome do Bem *"
              placeholder="Ex: Computador Dell"
            />
          </div>

          <div class="col-12 col-md-6">
            <q-select
              outlined
              dense
              v-model="form.categoria"
              :options="categorias"
              use-input
              new-value-mode="add-unique"
              label="Categoria do Bem *"
            />
          </div>

          <div class="col-12 col-md-6">
            <q-input
              outlined
              dense
              v-model="form.codigo"
              label="Código Interno do Bem *"
              placeholder="BEM-0500"
            />
          </div>

          <div class="col-12 col-md-6">
            <q-input
              outlined
              dense
              v-model="form.patrimonio"
              label="Número de Patrimônio"
              placeholder="PAT-2024-0112"
            />
          </div>

          <div class="col-12">
            <q-input
              outlined
              dense
              v-model="form.serie"
              label="Número de Série"
              placeholder="SN-AB123456"
            />
          </div>
        </div>

        <!-- =====================================================
             FABRICANTE
        ====================================================== -->

        <div class="titulo-secao q-mt-lg">INFORMAÇÕES DO FABRICANTE</div>

        <div class="row q-col-gutter-md">
          <div class="col-12 col-md-6">
            <q-input outlined dense v-model="form.fabricante" label="Fabricante *" />
          </div>

          <div class="col-12 col-md-6">
            <q-input outlined dense v-model="form.modelo" label="Modelo *" />
          </div>
        </div>

        <!-- =====================================================
             DESCRIÇÃO
        ====================================================== -->

        <div class="titulo-secao q-mt-lg">DETALHES DO ATIVO</div>

        <q-input
          outlined
          type="textarea"
          rows="4"
          v-model="form.descricao"
          label="Descrição do Bem"
        />

        <!-- =====================================================
             ADMINISTRATIVO
        ====================================================== -->

        <div class="titulo-secao q-mt-lg">INFORMAÇÕES ADMINISTRATIVAS</div>

        <div class="row q-col-gutter-md">
          <div class="col-12 col-md-6">
            <q-select
              outlined
              dense
              v-model="form.status"
              :options="statusOptions"
              label="Status do Bem *"
            />
          </div>

          <div class="col-12 col-md-6">
            <q-input
              outlined
              dense
              v-model="form.dataAquisicao"
              mask="##/##/####"
              label="Data de Aquisição *"
            >
              <template #append>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy cover transition-show="scale" transition-hide="scale">
                    <q-date v-model="form.dataAquisicao" mask="DD/MM/YYYY">
                      <div class="row justify-end q-pa-sm">
                        <q-btn flat color="primary" label="Fechar" v-close-popup />
                      </div>
                    </q-date>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>
          </div>

          <div class="col-12 col-md-6">
            <q-select
              outlined
              dense
              v-model="form.escritorioId"
              :options="escritorios"
              option-label="nome"
              option-value="id"
              emit-value
              map-options
              label="Escritório *"
            />
          </div>

          <div class="col-12 col-md-6">
            <q-select
              outlined
              dense
              v-model="form.departamentoId"
              :options="departamentos"
              option-label="nome"
              option-value="id"
              emit-value
              map-options
              label="Departamento *"
            />
          </div>

          <div class="col-12">
            <q-select
              outlined
              dense
              use-input
              fill-input
              hide-selected
              v-model="form.pessoaId"
              :options="responsaveis"
              option-label="nome"
              option-value="id"
              emit-value
              map-options
              label="Funcionário Responsável"
            />
          </div>
        </div>
      </q-card-section>

      <q-separator />

      <!-- =====================================================
           RODAPÉ
      ====================================================== -->

      <q-card-actions align="right" class="q-pa-md">
        <q-btn flat color="grey-8" label="Cancelar" @click="fecharFormulario" />

        <q-btn
          color="positive"
          unelevated
          :icon="modoEdicao ? 'save' : 'check_circle'"
          :label="modoEdicao ? 'Salvar Alterações' : 'Cadastrar Bem'"
          :loading="salvando"
          :disable="salvando"
          @click="salvarBem"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

import { useQuasar } from 'quasar'
import { api } from '@/boot/axios'

const $q = useQuasar()
const salvando = ref(false)

/* ==========================================================
   PROPS / EMITS
========================================================== */

const props = defineProps({
  /*
   * Controla abertura do modal.
   */
  modelValue: {
    type: Boolean,
    default: false,
  },

  /*
   * Bem que será editado.
   *
   * Quando for null:
   * → cadastro
   *
   * Quando possuir um objeto:
   * → edição
   */
  bem: {
    type: Object,
    default: null,
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
})

const emit = defineEmits(['update:modelValue', 'salvo'])

/* ==========================================================
   MODO
========================================================== */

const modoEdicao = computed(() => {
  return !!props.bem
})

/* ==========================================================
   FORMULÁRIO
========================================================== */

const form = ref({
  id: null,

  nome: '',
  categoria: null,
  codigo: '',
  patrimonio: '',
  serie: '',

  fabricante: '',
  modelo: '',

  descricao: '',

  status: null,
  dataAquisicao: '',

  escritorioId: null,
  departamentoId: null,
  pessoaId: null,
})

/* ==========================================================
   MOcks
========================================================== */

const categorias = computed(() => props.opcoes.categorias || [])
const statusOptions = computed(() => [
  ...new Set([...(props.opcoes.status || []), 'Ativo', 'Em Manutenção', 'Inativo', 'Descartado']),
])

const escritorios = computed(() => props.opcoes.escritorios || [])
const departamentos = computed(() => props.opcoes.departamentos || [])
const responsaveis = computed(() => props.opcoes.pessoas || [])

/* ==========================================================
   LIMPAR FORMULÁRIO
========================================================== */

function limparFormulario() {
  form.value = {
    id: null,

    nome: '',
    categoria: null,
    codigo: '',
    patrimonio: '',
    serie: '',

    fabricante: '',
    modelo: '',

    descricao: '',

    status: null,
    dataAquisicao: '',

    escritorioId: null,
    departamentoId: null,
    pessoaId: null,
  }
}

/* ==========================================================
   CARREGAR BEM PARA EDIÇÃO
========================================================== */

/*
 * Quando o usuário clicar no botão de editar,
 * o componente pai enviará o objeto do bem através
 * da prop "bem".
 *
 * Exemplo:
 *
 * bem = {
 *   id: 15,
 *   nome: 'Notebook Dell',
 *   categoria: 'Notebooks',
 *   ...
 * }
 *
 * O formulário será preenchido automaticamente.
 */

function carregarBem(bem) {
  if (!bem) {
    limparFormulario()

    return
  }

  form.value = {
    id: bem.id ?? null,

    nome: bem.nome ?? bem.nomeItem ?? '',

    categoria: bem.categoria ?? bem.categoriaBem ?? bem.tipo ?? null,

    codigo: bem.codigo ?? bem.codigoInterno ?? '',

    patrimonio: bem.patrimonio ?? '',

    serie: bem.serie ?? bem.serial ?? bem.numeroSerie ?? '',

    fabricante: bem.fabricante ?? bem.marca ?? '',

    modelo: bem.modelo ?? '',

    descricao: bem.descricao ?? bem.descricaoBem ?? '',

    status: bem.status ?? null,

    dataAquisicao: bem.dataAquisicao ? bem.dataAquisicao.split('-').reverse().join('/') : '',

    escritorioId: bem.escritorioId ?? null,
    departamentoId: bem.departamentoId ?? null,
    pessoaId: bem.pessoaId ?? null,
  }
}

/* ==========================================================
   WATCH
========================================================== */

/*
 * Observa o bem recebido pelo componente.
 *
 * Se o usuário clicar em editar:
 *
 * bem muda
 * ↓
 * carregarBem()
 * ↓
 * formulário é preenchido
 */

watch(
  () => props.bem,

  (novoBem) => {
    carregarBem(novoBem)
  },

  {
    immediate: true,
  },
)

/* ==========================================================
   FECHAR
========================================================== */

function fecharFormulario() {
  emit('update:modelValue', false)
}

/* ==========================================================
   SALVAR
========================================================== */

async function salvarBem() {
  /* ========================================================
     VALIDAÇÃO
  ======================================================== */

  const faltando = [
    ['nome', 'nome do bem'],
    ['categoria', 'categoria'],
    ['codigo', 'código interno'],
    ['fabricante', 'fabricante'],
    ['modelo', 'modelo'],
    ['status', 'status'],
    ['dataAquisicao', 'data de aquisição'],
    ['escritorioId', 'escritório'],
    ['departamentoId', 'departamento'],
  ]
    .filter(([campo]) => !String(form.value[campo] ?? '').trim())
    .map(([, rotulo]) => rotulo)

  if (faltando.length) {
    $q.notify({
      type: 'negative',
      message: `Preencha: ${faltando.join(', ')}.`,
    })
    return
  }

  const dataValida = /^(0[1-9]|[12]\d|3[01])\/(0[1-9]|1[0-2])\/\d{4}$/.test(
    form.value.dataAquisicao,
  )
  if (!dataValida) {
    $q.notify({ type: 'negative', message: 'Informe a data de aquisição no formato DD/MM/AAAA.' })
    return
  }

  salvando.value = true
  try {
    const [dia, mes, ano] = form.value.dataAquisicao.split('/')
    const payload = {
      codigo: form.value.codigo.trim(),
      patrimonio: form.value.patrimonio || null,
      nome: form.value.nome.trim(),
      categoria: form.value.categoria,
      serial: form.value.serie || null,
      fabricante: form.value.fabricante.trim(),
      modelo: form.value.modelo.trim(),
      descricao: form.value.descricao || null,
      status: form.value.status,
      dataAquisicao: `${ano}-${mes}-${dia}`,
      escritorioId: form.value.escritorioId,
      departamentoId: form.value.departamentoId,
      pessoaId: form.value.pessoaId || null,
    }
    const { data } = modoEdicao.value
      ? await api.put(`/bens/${form.value.id}`, payload)
      : await api.post('/bens', payload)

    $q.notify({
      type: 'positive',
      message: modoEdicao.value ? 'Bem atualizado com sucesso.' : 'Bem cadastrado com sucesso.',
    })
    emit('salvo', data)
    fecharFormulario()
    limparFormulario()
  } catch (error) {
    const resposta = error.response?.data
    const statusHttp = error.response?.status
    const mensagem =
      resposta?.mensagem ||
      (statusHttp === 401
        ? 'Sua sessão expirou. Entre novamente antes de cadastrar o bem.'
        : statusHttp === 403
          ? 'Sua conta não tem permissão para cadastrar este bem.'
          : statusHttp === 409
            ? 'O código interno já está cadastrado ou os dados estão vinculados a outro registro.'
            : statusHttp === 400
              ? (resposta &&
                  Object.entries(resposta)
                    .filter(([campo]) => campo !== 'mensagem')
                    .map(([campo, detalhe]) => `${campo}: ${detalhe}`)
                    .join(' ')) ||
                'Confira os campos obrigatórios e as opções selecionadas.'
              : statusHttp
                ? `Falha ao salvar o bem (HTTP ${statusHttp}).`
                : 'Não foi possível conectar ao backend. Verifique se ele está iniciado.')
    $q.notify({
      type: 'negative',
      message: mensagem,
    })
  } finally {
    salvando.value = false
  }
}
</script>
