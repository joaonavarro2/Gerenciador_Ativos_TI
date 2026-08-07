<template>

  <q-dialog
    v-model="dialogExportar"
    persistent
  >

    <q-card class="exportar-dialog">

      <!-- =====================================================
           HEADER
      ====================================================== -->

      <q-card-section class="row items-center justify-between">

        <div class="row items-center">

          <q-avatar
            color="blue-1"
            text-color="primary"
            icon="download"
            size="50px"
          />

          <div class="q-ml-md">

            <div class="text-h5 text-weight-bold">
              Exportar Relatório
            </div>

            <div class="text-grey-7">
              Gere um relatório completo utilizando os filtros atualmente aplicados.
            </div>

          </div>

        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          v-close-popup
        />

      </q-card-section>

      <q-separator />

      <!-- =====================================================
           CONTEÚDO
      ====================================================== -->

      <q-card-section class="scroll exportar-content">

        <!-- =====================================================
             FORMATO
        ====================================================== -->

        <div class="titulo-secao">

          FORMATO DO RELATÓRIO

        </div>

        <q-option-group

          v-model="formato"

          :options="formatos"

          color="primary"

        />

        <!-- =====================================================
             O QUE EXPORTAR
        ====================================================== -->

        <div class="titulo-secao q-mt-lg">

          DADOS

        </div>

        <q-option-group

          v-model="modoExportacao"

          :options="opcoesExportacao"

          color="primary"

        />

        <!-- =====================================================
             FILTROS
        ====================================================== -->

        <div class="titulo-secao q-mt-lg">

          FILTROS UTILIZADOS

        </div>

        <q-list bordered separator class="rounded-borders">

          <q-item>

            <q-item-section avatar>
              <q-icon name="search"/>
            </q-item-section>

            <q-item-section>

              <q-item-label>
                Pesquisa
              </q-item-label>

              <q-item-label caption>

                {{ filtros.busca || 'Nenhuma pesquisa aplicada' }}

              </q-item-label>

            </q-item-section>

          </q-item>

          <q-item>

            <q-item-section avatar>
              <q-icon name="category"/>
            </q-item-section>

            <q-item-section>

              <q-item-label>

                Categoria

              </q-item-label>

              <q-item-label caption>

                {{ filtros.categoria || 'Todas' }}

              </q-item-label>

            </q-item-section>

          </q-item>

          <q-item>

            <q-item-section avatar>
              <q-icon name="verified"/>
            </q-item-section>

            <q-item-section>

              <q-item-label>

                Status

              </q-item-label>

              <q-item-label caption>

                {{ filtros.status || 'Todos' }}

              </q-item-label>

            </q-item-section>

          </q-item>

          <q-item>

            <q-item-section avatar>
              <q-icon name="apartment"/>
            </q-item-section>

            <q-item-section>

              <q-item-label>

                Departamento

              </q-item-label>

              <q-item-label caption>

                {{ filtros.departamento || 'Todos' }}

              </q-item-label>

            </q-item-section>

          </q-item>

        </q-list>

        <!-- =====================================================
             RESUMO
        ====================================================== -->

        <div class="titulo-secao q-mt-lg">

          RESUMO

        </div>

        <q-banner

          rounded

          class="bg-blue-1 text-primary"

        >

          <div class="text-subtitle1 text-weight-bold">

            {{ quantidadeItens }} bens serão exportados.

          </div>

          <div class="text-caption">

            O relatório conterá exatamente os itens exibidos após a aplicação dos filtros selecionados.

          </div>

        </q-banner>

      </q-card-section>

      <q-separator />

      <!-- =====================================================
           RODAPÉ
      ====================================================== -->

      <q-card-actions
        align="right"
        class="q-pa-md"
      >

        <q-btn

          flat

          color="grey-8"

          label="Cancelar"

          v-close-popup

        />

        <q-btn

          color="primary"

          icon="download"

          label="Exportar"

          @click="exportarRelatorio"

        />

      </q-card-actions>

    </q-card>

  </q-dialog>

</template>

<script setup>

import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

/* ======================================================
   PROPS
====================================================== */

const props = defineProps({

  modelValue: Boolean,

  itens: {
    type: Array,
    default: () => []
  },

  filtros: {
    type: Object,
    default: () => ({
      busca: '',
      categoria: '',
      status: '',
      departamento: ''
    })
  }

})

const emit = defineEmits([
  'update:modelValue'
])

/* ======================================================
   DIALOG
====================================================== */

const dialogExportar = computed({

  get: () => props.modelValue,

  set: value => emit('update:modelValue', value)

})

/* ======================================================
   FORMATO
====================================================== */

const formato = ref('pdf')

const formatos = [

  {
    label: 'PDF (.pdf)',
    value: 'pdf'
  },

  {
    label: 'Excel (.xlsx)',
    value: 'excel'
  },

  {
    label: 'CSV (.csv)',
    value: 'csv'
  }

]

/* ======================================================
   EXPORTAÇÃO
====================================================== */

const modoExportacao = ref('filtrados')

const opcoesExportacao = [

  {
    label: 'Exportar somente os itens filtrados',
    value: 'filtrados'
  },

  {
    label: 'Exportar todos os bens cadastrados',
    value: 'todos'
  }

]

/* ======================================================
   QUANTIDADE
====================================================== */

const quantidadeItens = computed(() => {

  return props.itens.length

})

/* ======================================================
   BACKEND
======================================================

Hoje:

Os dados chegam através da prop:

props.itens

que é exatamente o resultado da tabela.

Se existirem filtros aplicados,
a tabela já envia somente os itens filtrados.

No futuro poderá existir:

POST /api/bens/exportar

Body:

{

   formato:"pdf",

   exportarTodos:false,

   filtros:{...}

}

A API retornará:

application/pdf

application/vnd.ms-excel

text/csv

====================================================== */

/* ======================================================
   PDF
====================================================== */

function exportarPDF() {

  console.log('Exportando PDF...')

  console.table(props.itens)

  $q.notify({

    type:'positive',

    message:'PDF gerado (mock).'

  })

}

/* ======================================================
   EXCEL
====================================================== */

function exportarExcel() {

  console.log('Exportando Excel...')

  console.table(props.itens)

  $q.notify({

    type:'positive',

    message:'Excel gerado (mock).'

  })

}

/* ======================================================
   CSV
====================================================== */

function exportarCSV() {

  console.log('Exportando CSV...')

  console.table(props.itens)

  $q.notify({

    type:'positive',

    message:'CSV gerado (mock).'

  })

}

/* ======================================================
   EXPORTAÇÃO
====================================================== */

function exportarRelatorio() {

  switch (formato.value) {

    case 'pdf':

      exportarPDF()

      break

    case 'excel':

      exportarExcel()

      break

    case 'csv':

      exportarCSV()

      break

  }

  dialogExportar.value = false

}

</script>
