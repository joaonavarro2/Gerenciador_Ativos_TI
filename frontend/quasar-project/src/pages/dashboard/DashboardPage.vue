<template>

  <q-page class="dashboard-page">

    <DashboardHeader />

    <DashboardSummary />

    <DashboardCharts />

    <DashboardAssetsTable />

  </q-page>

</template>

<script setup>
import DashboardHeader from '@/components/dashboard/DashboardHeader.vue'
import DashboardSummary from '@/components/dashboard/DashboardSummary.vue'
import DashboardCharts from '@/components/dashboard/DashboardCharts.vue'
import DashboardAssetsTable from '@/components/dashboard/DashboardAssetsTable.vue'
</script>