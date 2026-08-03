<template>

  <div class="bem-info-grid">

    <!-- =====================================================
         COLUNA ESQUERDA
    ====================================================== -->

    <div class="info-column">

      <div class="info-item">

        <q-icon
          name="tag"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            PATRIMÔNIO
          </div>

          <div class="info-value">
            BEM-0041
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="inventory_2"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            DESCRIÇÃO
          </div>

          <div class="info-value">
            Trator Agrícola MF 275
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="category"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            CATEGORIA
          </div>

          <div class="info-value">
            Veículo
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="business"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            MARCA
          </div>

          <div class="info-value">
            Massey Ferguson
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="precision_manufacturing"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            MODELO
          </div>

          <div class="info-value">
            MF 275
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="qr_code"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            NÚMERO DE SÉRIE
          </div>

          <div class="info-value">
            MF275-2023-001
          </div>

        </div>

      </div>

    </div>

    <!-- =====================================================
         COLUNA DIREITA
    ====================================================== -->

    <div class="info-column">

      <div class="info-item">

        <q-icon
          name="event"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            DATA DE AQUISIÇÃO
          </div>

          <div class="info-value">
            18/03/2023
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="apartment"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            DEPARTAMENTO
          </div>

          <div class="info-value">
            Operações
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="person"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            RESPONSÁVEL
          </div>

          <div class="info-value">
            Carlos Andrade
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="place"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            LOCALIZAÇÃO
          </div>

          <div class="info-value">
            Galpão Central
          </div>

        </div>

      </div>

      <div class="info-item">

        <q-icon
          name="check_circle"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            STATUS
          </div>

          <div class="info-value status-ok">
            Ativo
          </div>

        </div>

      </div>

      <div class="info-item align-top">

        <q-icon
          name="description"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            OBSERVAÇÕES
          </div>

          <div class="info-value">
            Equipamento utilizado nas operações agrícolas da unidade central.
          </div>

        </div>

      </div>

    </div>

  </div>

</template>

<script setup>

/*
======================================================

BACKEND

GET /api/bens/{id}

Todos estes campos serão preenchidos
automaticamente pela API.

Por enquanto são apenas exemplos
visuais para montagem da interface.

======================================================
*/

</script>