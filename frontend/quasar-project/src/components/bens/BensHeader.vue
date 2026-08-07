<template>
  <q-card flat bordered class="bens-header">

    <!-- Topo -->
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

        <q-btn
        @click="exportarBem"
          outline
          color="grey-8"
          icon="download"
          label="Exportar"
          no-caps
        />
        <ExportarRelatorio 
          v-model="dialogExportar"/>

        <q-btn  @click="cadastrarBem"
          color="positive"
          icon="add"
          label="Novo Bem"
          no-caps
        />
        <BemCadastro 
          v-model="dialogCadastro "
        />
      </div>

    </div>

    <!-- Pesquisa -->
    <div class="row q-col-gutter-md">

      <div class="col">
        <q-input
          outlined
          dense
          placeholder="Buscar por ID, descrição, número de série..."
        >
          <template #prepend>
            <q-icon name="search" />
          </template>
        </q-input>
      </div>

      <div class="col-auto">
        <q-select
          outlined
          dense
          style="width: 140px"
          :options="['PC', 'Notebook', 'Monitor', 'Impressora', 'Scanner', 'Perifericos', 'Infraestrutura']"
          model-value="Tipo de Bem"
        />
      </div>

      <div class="col-auto">
        <q-select
          outlined
          dense
          style="width: 140px"
          :options="['Ativo', 'Inativo', 'Manutenção', 'Descartado']"
          model-value="Status"
        />
      </div>

      <div class="col-auto">
        <q-select
          outlined
          dense
          style="width: 160px"
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
          'Coord.Articulação de Políticas',
          ]"
          model-value="Departamento"
        />
      </div>

    </div>

  </q-card>
</template>

<script setup>
  import { ref } from 'vue'
  import BemCadastro from './cadastro/Cadastro.vue'
  import ExportarRelatorio from './exportacao/ExportarRelatorio.vue'
  const dialogCadastro = ref(false)
  const dialogExportar = ref(false)

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


</script>
