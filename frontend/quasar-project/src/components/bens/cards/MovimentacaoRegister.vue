<template>

  <q-dialog
    :model-value="modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="$emit('update:modelValue', $event)"
  >

    <q-card class="mov-dialog" style="
    width:900px;
    height:500px;
    max-width:none;
    max-height:none;
    background:white;
    border-radius:10px;">

      <!-- =======================================================
           HEADER
      ======================================================== -->

      <div class="mov-header">

        <div>

          <div class="mov-title">
            Registrar Movimentação
          </div>

          <div class="mov-subtitle">

            Bem:

            <span class="mov-patrimonio">

              {{ bemSelecionado.patrimonio }}

            </span>

            -

            {{ bemSelecionado.nome }}

          </div>

        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          class="dialog-close"
          @click="$emit('update:modelValue', false)"
        />

      </div>

      <q-separator />

      <!-- =======================================================
           FORMULÁRIO
      ======================================================== -->

      <div class="mov-content">

        <div class="row q-col-gutter-lg">

          <!-- =============================
               BEM
          ============================== -->

          <div class="col-12 col-md-6">

            <q-select

              v-model="movimentacao.bem"

              outlined

              use-input

              fill-input

              hide-selected

              input-debounce="300"

              option-label="label"

              option-value="id"

              emit-value

              map-options

              :options="bens"

              label="Bem"

              @filter="buscarBens"

            />

          </div>

          <!-- =============================
               TIPO
          ============================== -->

          <div class="col-12 col-md-6">

            <q-select

              v-model="movimentacao.tipo"

              outlined

              :options="tiposMovimentacao"

              label="Tipo de Movimentação"

            />

          </div>

          <!-- =============================
               DATA
          ============================== -->

          <div class="col-12 col-md-6">

            <q-input

              outlined

              v-model="movimentacao.data"

              mask="##/##/####"

              label="Data da Movimentação"

            />

          </div>

          <!-- =============================
               DEPTO ORIGEM
          ============================== -->

          <div class="col-12 col-md-6">

            <q-input

              outlined

              readonly

              v-model="movimentacao.departamentoOrigem"

              label="Departamento de Origem"

            />

          </div>

          <!-- =============================
               DEPTO DESTINO
          ============================== -->

          <div class="col-12 col-md-6">

            <q-select

              outlined

              v-model="movimentacao.departamentoDestino"

              :options="departamentos"

              label="Departamento de Destino"

            />

          </div>

          <!-- =============================
               RESPONSÁVEL ENTREGA
          ============================== -->

          <div class="col-12 col-md-6">

            <q-select

              outlined

              use-input

              fill-input

              hide-selected

              input-debounce="300"

              option-label="label"

              option-value="id"

              emit-value

              map-options

              :options="responsaveis"

              label="Responsável pela Entrega"

              @filter="buscarResponsaveis"

            />

          </div>

          <!-- =============================
               RESPONSÁVEL RECEBIMENTO
          ============================== -->

          <div class="col-12 col-md-6">

            <q-select

              outlined

              use-input

              fill-input

              hide-selected

              input-debounce="300"

              option-label="label"

              option-value="id"

              emit-value

              map-options

              :options="responsaveis"

              label="Responsável pelo Recebimento"

              @filter="buscarResponsaveis"

            />

          </div>

          <!-- =============================
               JUSTIFICATIVA
          ============================== -->

          <div class="col-12">

            <q-input

              outlined

              autogrow

              type="textarea"

              v-model="movimentacao.justificativa"

              label="Justificativa / Motivo"

            />

          </div>

        </div>

      </div>

      <q-separator />

      <!-- =======================================================
           BOTÕES
      ======================================================== -->

      <div class="mov-footer">

        <q-btn

          outline

          color="grey-7"

          label="Cancelar"

          @click="$emit('update:modelValue', false)"

        />

        <q-btn

          color="positive"

          icon="check_circle"

          label="Registrar Movimentação"

          @click="registrarMovimentacao"

        />

      </div>

    </q-card>

  </q-dialog>

</template>

<script setup>

import { ref } from 'vue'

/* ==========================================================
   PROPS
========================================================== */

defineProps({

  modelValue: Boolean,

  bemId: {
    type: String,
    default: null
  }

})

const emit = defineEmits([
  'update:modelValue'
])

/* ==========================================================
   BACKEND

   GET /api/bens/{id}

   Quando o usuário clicar no botão de movimentação,
   este endpoint carregará automaticamente todas as
   informações do bem.

========================================================== */

const bemSelecionado = ref({

  id: 1,

  patrimonio: 'BEM-0041',

  nome: 'Trator Agrícola MF 275',

  departamento: 'Operações'

})

/* ==========================================================
   FORMULÁRIO
========================================================== */

const movimentacao = ref({

  bem: null,

  tipo: null,

  data: '',

  departamentoOrigem: 'Operações',

  departamentoDestino: null,

  responsavelEntrega: null,

  responsavelRecebimento: null,

  justificativa: ''

})

/* ==========================================================
   MOCK - BENS

   BACKEND

   GET /api/bens/search?termo=

   Esta lista será substituída
   pela resposta da API.

========================================================== */

const bens = ref([

  {

    id: 1,

    label: 'BEM-0041 - Trator Agrícola MF 275'

  },

  {

    id: 2,

    label: 'BEM-0098 - Notebook Dell Latitude'

  },

  {

    id: 3,

    label: 'BEM-0156 - Impressora HP LaserJet'

  }

])

/* ==========================================================
   MOCK - TIPOS

   BACKEND

   GET /api/movimentacoes/tipos

========================================================== */

const tiposMovimentacao = ref([

  'Transferência',

  'Empréstimo',

  'Devolução',

  'Conserto',

  'Baixa'

])

/* ==========================================================
   MOCK - DEPARTAMENTOS

   BACKEND

   GET /api/departamentos

========================================================== */

const departamentos = ref([

  'Operações',

  'Agronomia',

  'Financeiro',

  'TI',

  'Administrativo'

])

/* ==========================================================
   MOCK - RESPONSÁVEIS

   BACKEND

   GET /api/pessoas/search?nome=

========================================================== */

const responsaveis = ref([

  {

    id: 1,

    label: 'Carlos Andrade'

  },

  {

    id: 2,

    label: 'Rafael Oliveira'

  },

  {

    id: 3,

    label: 'Mariana Souza'

  }

])

/* ==========================================================
   AUTOCOMPLETE DOS BENS

   BACKEND

   GET /api/bens/search?termo={texto}

   O backend retornará apenas os bens
   compatíveis com o texto digitado.

========================================================== */

function buscarBens(val, update) {

  update(() => {

    /*
    Quando existir backend:

    const response = await api.get(
      '/api/bens/search',
      {
        params:{
          termo: val
        }
      }
    )

    bens.value = response.data

    */

  })

}

/* ==========================================================
   AUTOCOMPLETE RESPONSÁVEIS

   BACKEND

   GET /api/pessoas/search?nome={texto}

========================================================== */

function buscarResponsaveis(val, update) {

  update(() => {

    /*
    Quando existir backend:

    const response = await api.get(
      '/api/pessoas/search',
      {
        params:{
          nome: val
        }
      }
    )

    responsaveis.value = response.data

    */

  })

}

/* ==========================================================
   REGISTRAR MOVIMENTAÇÃO

   BACKEND

   POST /api/movimentacoes

========================================================== */

function registrarMovimentacao() {

  /*
  =====================================================

  POST /api/movimentacoes

  Body esperado:

  {

    bemId,

    tipo,

    data,

    departamentoDestino,

    responsavelEntrega,

    responsavelRecebimento,

    justificativa

  }

  =====================================================
  */

  console.log(movimentacao.value)

  emit('update:modelValue', false)

}

</script>