<template>

  <q-card flat bordered class="departamentos-header">

    <!-- =====================================================
         TOPO
    ====================================================== -->

    <div class="row items-start justify-between q-mb-lg">

      <!-- TÍTULO -->

      <div>

        <div class="text-h4 text-weight-bold">
          Departamentos Administrativos
        </div>

        <div class="text-subtitle1 text-grey-7 q-mt-xs">
          Departamentos responsáveis pela gestão dos bens e atividades administrativas da instituição.
        </div>

      </div>


      <!-- =================================================
           BOTÃO NOVO DEPARTAMENTO
      ================================================== -->

      <div class="row q-gutter-sm">

        <q-btn
          color="positive"
          icon="add"
          label="Novo Departamento"
          no-caps
          class="departamentos-btn-novo"
          @click="novoDepartamento"
        />

      </div>

    </div>


    <!-- =====================================================
         CARDS DE RESUMO
    ====================================================== -->

    <div class="row q-gutter-sm q-mb-lg">

      <!-- =================================================
           TOTAL DE DEPARTAMENTOS
      ================================================== -->

      <q-card
        flat
        bordered
        class="departamentos-resumo-card"
      >

        <div class="departamentos-resumo-icone departamentos-icone-total">

          <q-icon
            name="business"
            size="20px"
          />

        </div>

        <div>

          <div class="departamentos-resumo-numero">
            {{ totalDepartamentos }}
          </div>

          <div class="departamentos-resumo-label">
            Total de Departamentos
          </div>

        </div>

      </q-card>


      <!-- =================================================
           TOTAL DE BENS
      ================================================== -->

      <q-card
        flat
        bordered
        class="departamentos-resumo-card"
      >

        <div class="departamentos-resumo-icone departamentos-icone-bens">

          <q-icon
            name="inventory_2"
            size="20px"
          />

        </div>

        <div>

          <div class="departamentos-resumo-numero">
            {{ totalBens }}
          </div>

          <div class="departamentos-resumo-label">
            Total de Bens Registrados
          </div>

        </div>

      </q-card>

    </div>


    <!-- =====================================================
         FILTROS
    ====================================================== -->

    <div class="row q-col-gutter-md">

      <!-- =================================================
           BUSCA
      ================================================== -->

      <div class="col">

        <div class="departamentos-field-label">
          Buscar
        </div>

        <q-input
          v-model="busca"
          outlined
          dense
          clearable
          placeholder="Buscar por nome, sigla ou responsável..."
          class="departamentos-busca"
        >

          <template #prepend>

            <q-icon name="search" />

          </template>

        </q-input>

      </div>


      <!-- =================================================
           STATUS
      ================================================== -->

      <div class="col-auto">

        <div class="departamentos-field-label">
          Status
        </div>

        <q-select
          v-model="status"
          outlined
          dense
          clearable
          :options="statusOptions"
          label="Status"
          class="departamentos-filtro"
          style="width: 150px"
        />

      </div>

    </div>

  </q-card>

</template>


<script setup>

import { computed } from 'vue'


/* ==========================================================
   PROPS
========================================================== */

/*
 * Os valores dos cards vêm da DepartamentosPage.vue.
 *
 * Os mocks NÃO ficam neste componente.
 *
 * Futuramente esses valores poderão ser
 * calculados com os dados vindos da API.
 */

const props = defineProps({

  /* ========================================================
     RESUMO
  ======================================================== */

  totalDepartamentos: {
    type: Number,
    default: 0
  },

  totalBens: {
    type: Number,
    default: 0
  },

  


  /* ========================================================
     FILTROS
  ======================================================== */

  filtros: {
    type: Object,
    required: true
  }

})


/* ==========================================================
   EMITS
========================================================== */

/*
 * update:filtros
 * --------------
 * Atualiza os filtros na DepartamentosPage.
 *
 * novo-departamento
 * -----------------
 * Informa à Page que o botão
 * "Novo Departamento" foi clicado.
 */

const emit = defineEmits([

  'update:filtros',

  'novo-departamento'

])


/* ==========================================================
   FILTROS
========================================================== */

/*
 * Cada computed funciona como uma ponte
 * entre o Header e o objeto filtros da Page.
 */


/* ==========================================================
   BUSCA
========================================================== */

const busca = computed({

  get() {

    return props.filtros.busca

  },

  set(valor) {

    emit(

      'update:filtros',

      {
        ...props.filtros,
        busca: valor || ''
      }

    )

  }

})


/* ==========================================================
   STATUS
========================================================== */

const status = computed({

  get() {

    return props.filtros.status

  },

  set(valor) {

    emit(

      'update:filtros',

      {
        ...props.filtros,
        status: valor || null
      }

    )

  }

})


/* ==========================================================
   OPÇÕES DE STATUS
========================================================== */

const statusOptions = [

  'Ativo',

  'Inativo'

]


/* ==========================================================
   NOVO DEPARTAMENTO
========================================================== */

/*
 * O Header não abre nenhum Dialog diretamente.
 *
 * Ele apenas avisa a DepartamentosPage.
 */

function novoDepartamento() {

  emit('novo-departamento')

}

</script>