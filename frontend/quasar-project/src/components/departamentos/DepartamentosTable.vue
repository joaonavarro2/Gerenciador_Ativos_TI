<template>

  <div class="departamentos-table">

    <!-- =====================================================
         GRADE DE DEPARTAMENTOS
    ====================================================== -->

    <div class="departamentos-grid">

      <q-card
        v-for="departamento in departamentos"
        :key="departamento.id"
        flat
        bordered
        class="departamento-card"
        @click="visualizarDepartamento(departamento)"
      >

        <!-- =================================================
             SIGLA
        ================================================== -->

        <div
          class="departamento-sigla"
          :style="{
            backgroundColor: departamento.corFundo,
            color: departamento.cor
          }"
        >

          {{ departamento.sigla }}

        </div>


        <!-- =================================================
             NOME
        ================================================== -->

        <div class="departamento-nome">

          {{ departamento.nome }}

        </div>


        <!-- =================================================
             DESCRIÇÃO
        ================================================== -->

        <div class="departamento-descricao">

          {{ departamento.descricao }}

        </div>


        <!-- =================================================
             INFORMAÇÕES
        ================================================== -->

        <div class="departamento-informacoes">

          <!-- =================================================
               BENS
          ================================================== -->

          <div class="departamento-informacao">

            <div
              class="departamento-informacao-icone"
              :style="{
                backgroundColor: departamento.corFundo,
                color: departamento.cor
              }"
            >

              <q-icon
                name="inventory_2"
                size="14px"
              />

            </div>

            <div class="departamento-informacao-texto">

              <strong>
                {{ departamento.bens }}
              </strong>

              <span>
                Bens
              </span>

            </div>

          </div>


          <!-- =================================================
               CONSERTOS
          ================================================== -->

          <div class="departamento-informacao">

            <div
              class="departamento-consertos-icone"
              :style="{
                backgroundColor: departamento.corFundo,
                color: departamento.cor
              }"
            >

              <q-icon
                name="build"
                size="14px"
              />

            </div>

            <div class="departamento-informacao-texto">

              <strong>
                {{ departamento.consertos }}
              </strong>

              <span>
                Consertos
              </span>

            </div>

          </div>

        </div>


        <!-- =================================================
             BARRA INFERIOR
        ================================================== -->

        <div
          class="departamento-barra"
          :style="{
            backgroundColor: departamento.cor
          }"
        />

      </q-card>

    </div>

  </div>

</template>

<script setup>

/* ==========================================================
   PROPS
========================================================== */

/*
 * Os departamentos são recebidos da
 * DepartamentosPage.vue.
 *
 * Inicialmente:
 * - utilizamos os mocks da Page.
 *
 * Futuramente:
 * - os dados poderão vir da API.
 */

defineProps({

  departamentos: {
    type: Array,
    default: () => []
  }

})


/* ==========================================================
   EMITS
========================================================== */

/*
 * Quando um departamento for clicado,
 * este evento será enviado para
 * DepartamentosPage.vue.
 *
 * A Page será responsável por abrir
 * o VisualizacaoDepartamento.vue.
 */

const emit = defineEmits([

  'visualizar-departamento'

])


/* ==========================================================
   VISUALIZAR DEPARTAMENTO
========================================================== */

/*
 * Recebe o departamento clicado
 * e envia para a Page.
 */

function visualizarDepartamento(departamento) {

  emit(
    'visualizar-departamento',
    departamento
  )

}

</script>