<template>

  <!-- ==========================================================
       BACKEND

       GET /api/dashboard/charts

       {
          monthlyActivity: [],
          categoryDistribution: []
       }

       Os gráficos serão alimentados pela API.

       Inicialmente o componente apenas reserva o espaço.
  =========================================================== -->

  <div class="dashboard-charts">

    <!-- ============================= -->
    <!-- Atividade Mensal -->
    <!-- ============================= -->

    <q-card class="dashboard-chart-card">

      <div class="dashboard-chart-header">

        <div>

          <h3 class="dashboard-chart-title">
            Atividade Mensal
          </h3>

          <p class="dashboard-chart-subtitle">
            Movimentações e consertos por mês — 2026
          </p>

        </div>

      </div>

      <div class="dashboard-chart-body">

        <!-- Gráfico virá aqui -->

        <div class="chart-placeholder">

          <q-icon
            name="bar_chart"
            size="70px"
            class="chart-placeholder-icon"
          />

          <span>
            Gráfico aguardando dados do servidor
          </span>

        </div>

      </div>

    </q-card>

    <!-- ============================= -->
    <!-- Distribuição -->
    <!-- ============================= -->

    <q-card class="dashboard-chart-card">

      <div class="dashboard-chart-header">

        <div>

          <h3 class="dashboard-chart-title">
            Distribuição por Categoria
          </h3>

          <p class="dashboard-chart-subtitle">
            Composição do patrimônio ativo
          </p>

        </div>

      </div>

      <div class="dashboard-chart-body">

        <div class="chart-placeholder">

          <q-icon
            name="donut_large"
            size="70px"
            class="chart-placeholder-icon"
          />

          <span>
            Gráfico aguardando dados do servidor
          </span>

        </div>

      </div>

    </q-card>

  </div>

</template>

<script setup>

/*

Futuramente:

import ApexCharts from 'vue3-apexcharts'

ou

import Chart from 'chart.js'

*/

</script>