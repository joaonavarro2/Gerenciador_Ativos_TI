<template>
  <q-card flat bordered class="bens-header">


    <!-- =====================================================
     TOPO
====================================================== -->

    <div class="row items-start justify-between q-mb-lg">

      <div>

        <div class="text-h4 text-weight-bold">
          Inventário de Bens
        </div>

        <div class="text-subtitle1 text-grey-7 q-mt-xs">
          Registro completo de todos os bens e equipamentos cadastrados.
        </div>

      </div>


      <div class="row q-gutter-sm">

        <!-- Exportar -->
        <q-btn @click="exportarBem" outline color="grey-8" icon="download" label="Exportar" no-caps />

        <!--
      IMPORTANTE:
      Envia os filtros atuais para o componente
      de exportação.
    -->
        <ExportarRelatorio v-model="dialogExportar" :filtros="filtros"  :itens="props.itens" />


        <!-- Novo bem -->
        <q-btn @click="cadastrarBem" color="positive" icon="add" label="Novo Bem" no-caps />

        <BemCadastro v-model="dialogCadastro" />

      </div>

    </div>


    <!-- =====================================================
     FILTROS
====================================================== -->

    <div class="row q-col-gutter-md">


      <!-- ===================================================
       PESQUISA
  ==================================================== -->

      <div class="col">

        <q-input v-model="filtros.busca" outlined dense placeholder="Buscar por ID, descrição, número de série..."
          clearable>

          <template #prepend>

            <q-icon name="search" />

          </template>

        </q-input>

      </div>


      <!-- ===================================================
       CATEGORIA
  ==================================================== -->

      <div class="col-auto">

        <q-select v-model="filtros.categoria" multiple outlined dense clearable style="width: 140px" label="Tipo de Bem" 
          :options="[
            'PC',
            'Notebook',
            'Monitor',
            'Impressora',
            'Scanner',
            'Perifericos',
            'Infraestrutura'
          ]" />

      </div>


      <!-- ===================================================
       STATUS
  ==================================================== -->

      <div class="col-auto">

        <q-select v-model="filtros.status" multiple outlined dense clearable style="width: 140px" label="Status" :options="[
          'Ativo',
          'Inativo',
          'Manutenção',
          'Descartado'
        ]" />

      </div>


      <!-- ===================================================
       DEPARTAMENTO
  ==================================================== -->

      <div class="col-auto">

        <q-select v-model="filtros.departamento" multiple outlined dense clearable style="width: 160px" label="Departamento"
          :options="[
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
            'Coord.Articulação de Políticas'
          ]" />

      </div>

    </div>


    <!-- =====================================================
     FILTROS ATIVOS
====================================================== -->

    <div v-if="possuiFiltros" class="row items-center q-gutter-sm q-mt-md">

      <div class="text-caption text-grey-7">
        Filtros aplicados:
      </div>


      <!-- Pesquisa -->
      <q-chip v-if="filtros.busca" removable color="blue-1" text-color="primary" icon="search"
        @remove="filtros.busca = ''">
        Pesquisa: {{ filtros.busca }}
      </q-chip>


      <!-- Categoria -->
      <q-chip v-if="filtros.categoria" removable color="blue-1" text-color="primary" icon="category"
        @remove="filtros.categoria = ''">
        Categoria: {{ filtros.categoria }}
      </q-chip>


      <!-- Status -->
      <q-chip v-if="filtros.status" removable color="blue-1" text-color="primary" icon="verified"
        @remove="filtros.status = ''">
        Status: {{ filtros.status }}
      </q-chip>


      <!-- Departamento -->
      <q-chip v-if="filtros.departamento" removable color="blue-1" text-color="primary" icon="apartment"
        @remove="filtros.departamento = ''">
        Departamento: {{ filtros.departamento }}
      </q-chip>

    </div>
  


  </q-card>
</template>

<script setup>
import { ref,computed } from 'vue'
import BemCadastro from './cadastro/Cadastro.vue'
import ExportarRelatorio from '@/components/common/ExportarRelatorio.vue'
const dialogCadastro = ref(false)
const dialogExportar = ref(false)





const props = defineProps({

  itens: {
    type: Array,
    default: () => []
  }

})

function cadastrarBem() {

  /*
  ===================================== 

  BACKEND

  Apenas abre o formulário vazio.

  POST /api/bens

  O envio será realizado quando
  o usuário clicar em "Cadastrar Bem".

  =====================================
  */
  console.log('clicou')
  dialogCadastro.value = true

}

function exportarBem() {
  /*
  ===================================== 

  BACKEND

  Apenas abre o formulário de exportação.

  GET /api/bens/exportar

  O envio será realizado quando
  o usuário clicar em "Exportar".

  =====================================
  */
  console.log('clicou')
  dialogExportar.value = true
}


/* ======================================================
   FILTROS
====================================================== */

const filtros = ref({

  busca: '',

  categoria: '',

  status: '',

  departamento: ''

})


/* ======================================================
   VERIFICA SE EXISTE ALGUM FILTRO
====================================================== */

const possuiFiltros = computed(() => {

  return (

    filtros.value.busca.trim() !== '' ||

    filtros.value.categoria !== '' ||

    filtros.value.status !== '' ||

    filtros.value.departamento !== ''

  )

})



</script>
