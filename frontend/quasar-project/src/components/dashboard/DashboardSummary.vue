<template>
  <div class="dashboard-summary">

    <!-- ==========================================================
         BACKEND
         Os dados destes cards serão carregados pela API.

         Exemplo:
         GET /api/dashboard/summary

         {
           totalAssets,
           pendingMovements,
           maintenanceOpen,
           itemsInAnalysis
         }
    =========================================================== -->

    <!-- Card 1 -->
    <q-card class="summary-card">

      <div class="summary-card-top">

        <div>
          <div class="summary-title">
            Total de Bens
          </div>

          <div class="summary-value">
            —
          </div>
        </div>

        <div class="summary-icon bg-success-light">
          <q-icon
            name="inventory_2"
            class="icon-success"
          />
        </div>

      </div>

      <div class="summary-footer">

        <span class="summary-positive">
          —
        </span>

        <span class="summary-period">
          vs. mês anterior
        </span>

      </div>

    </q-card>

    <!-- Card 2 -->
    <q-card class="summary-card">

      <div class="summary-card-top">

        <div>

          <div class="summary-title">
            Movimentações Pendentes
          </div>

          <div class="summary-value">
            —
          </div>

        </div>

        <div class="summary-icon bg-warning-light">
          <q-icon
            name="sync_alt"
            class="icon-warning"
          />
        </div>

      </div>

      <div class="summary-footer">

        <span class="summary-negative">
          —
        </span>

        <span class="summary-period">
          vs. mês anterior
        </span>

      </div>

    </q-card>

    <!-- Card 3 -->
    <q-card class="summary-card">

      <div class="summary-card-top">

        <div>

          <div class="summary-title">
            Consertos Abertos
          </div>

          <div class="summary-value">
            —
          </div>

        </div>

        <div class="summary-icon bg-danger-light">
          <q-icon
            name="build"
            class="icon-danger"
          />
        </div>

      </div>

      <div class="summary-footer">

        <span class="summary-positive">
          —
        </span>

        <span class="summary-period">
          vs. mês anterior
        </span>

      </div>

    </q-card>

    <!-- Card 4 -->
    <q-card class="summary-card">

      <div class="summary-card-top">

        <div>

          <div class="summary-title">
            Itens em Análise
          </div>

          <div class="summary-value">
            —
          </div>

        </div>

        <div class="summary-icon bg-info-light">
          <q-icon
            name="pending_actions"
            class="icon-info"
          />
        </div>

      </div>

      <div class="summary-footer">

        <span class="summary-positive">
          —
        </span>

        <span class="summary-period">
          vs. mês anterior
        </span>

      </div>

    </q-card>

  </div>
</template>

<script setup>
// Os dados serão conectados futuramente via Pinia + Axios.
</script>