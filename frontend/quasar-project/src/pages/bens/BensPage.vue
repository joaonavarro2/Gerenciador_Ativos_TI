<template>

  <q-page class="page-container">

    <BensHeader /> 

    <BensFilters />
    
    <BensTable />

  </q-page>

</template>

<script setup>
import BensHeader from '@/components/bens/BensHeader.vue'
// import BensFilters from '@/components/bens/BensFilters.vue'
import BensTable from '@/components/bens/BensTable.vue'


</script>