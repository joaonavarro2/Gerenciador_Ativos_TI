<template>

  <q-page class="page-container">

    <BensHeader 
     :itens="itensParaExportacao"
    /> 

    <BensFilters />
    
    <BensTable 
     @bens-atualizados="receberBens"
    />

  </q-page>

</template>

<script setup>
import BensHeader from '@/components/bens/BensHeader.vue'
import BensTable from '@/components/bens/BensTable.vue'
import { ref } from 'vue'

const itensParaExportacao = ref([])

function receberBens(bens) {

  itensParaExportacao.value = bens

}


</script>