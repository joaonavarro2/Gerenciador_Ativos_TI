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
          outline
          color="grey-8"
          icon="download"
          label="Exportar"
          no-caps
        />

        <q-btn
          color="positive"
          icon="add"
          label="Novo Bem"
          no-caps
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
          :options="['Tipo de Bem']"
          model-value="Tipo de Bem"
        />
      </div>

      <div class="col-auto">
        <q-select
          outlined
          dense
          style="width: 140px"
          :options="['Status']"
          model-value="Status"
        />
      </div>

      <div class="col-auto">
        <q-select
          outlined
          dense
          style="width: 160px"
          :options="['Departamento']"
          model-value="Departamento"
        />
      </div>

    </div>

  </q-card>
</template>

<script setup>
</script>
