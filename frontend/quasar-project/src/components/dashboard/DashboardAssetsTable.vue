<template>

  <q-card flat class="dashboard-table">

    <!-- Cabeçalho -->
    <div class="table-header">

      <div>

        <h2>Registro de Bens</h2>
        <span>
          Dados fornecidos pela API
        </span>

      </div>

      <q-input
        outlined
        dense
        placeholder="Buscar..."
        class="table-search"
      >
        <template #prepend>
          <q-icon name="search"/>
        </template>
      </q-input>

    </div>

    <!-- Tabela -->

    <q-table
      flat
      :rows="rows"
      :columns="columns"
      row-key="id"
      hide-bottom
      separator="horizontal"
      class="assets-table"
    >

      <!-- STATUS -->

      <template #body-cell-status="props">

        <q-td :props="props">

          <q-badge
            rounded
            color="grey-3"
            text-color="grey-7"
          >

            {{ props.value || '--' }}

          </q-badge>

        </q-td>

      </template>

    </q-table>

  </q-card>

</template>

<script setup>

/*
======================================================
BACKEND

Este componente NÃO possui dados fixos.

Quando o backend estiver pronto:

GET /dashboard/ultimos-bens

preencherá:

rows

======================================================
*/

const rows = []

const columns = [

{
name:'id',
label:'ID',
field:'id',
align:'left'
},

{
name:'descricao',
label:'DESCRIÇÃO',
field:'descricao',
align:'left'
},

{
name:'unidade',
label:'UNIDADE',
field:'unidade'
},

{
name:'departamento',
label:'DEPARTAMENTO',
field:'departamento'
},

{
name:'data',
label:'DATA',
field:'data'
},

{
name:'status',
label:'STATUS',
field:'status'
}

]

</script>