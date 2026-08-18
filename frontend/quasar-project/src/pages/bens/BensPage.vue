<template>
  <q-page class="page-container">
    <BensHeader :itens="bens" />

    <BensTable :bens="bens" @bens-atualizados="receberBens" />
  </q-page>
</template>

<script setup>
import { ref } from 'vue'

import BensHeader from '@/components/bens/BensHeader.vue'
import BensTable from '@/components/bens/BensTable.vue'

// Fonte única dos dados da página. Futuramente será preenchida pelo Spring Boot.
const bens = ref([
  { id: 'BEM-001', descricao: 'Trator Agrícola MF 275', serie: 'MF275-2023-001', categoria: 'Veículo', marca: 'Massey Ferguson • MF 275', departamento: 'Operações', responsavel: 'Carlos Andrade', status: 'Inativo', icon: 'agriculture', avatarColor: 'green-1', iconColor: 'green-8' },
  { id: 'BEM-002', descricao: 'Lenovo ThinkPad X1 Carbon', serie: 'LEN-X1-2024-001', categoria: 'Notebook', marca: 'Lenovo • ThinkPad X1 Carbon', departamento: 'Dep. Recursos Humanos', responsavel: 'Mariana Santos', status: 'Ativo', icon: 'laptop', avatarColor: 'blue-1', iconColor: 'blue-8' },
  { id: 'BEM-003', descricao: 'Dell Latitude 7420', serie: 'DEL-7420-001', categoria: 'Notebook', marca: 'Dell • Latitude 7420', departamento: 'Dep. Financeiro', responsavel: 'João Silva', status: 'Em Manutenção', icon: 'laptop', avatarColor: 'orange-1', iconColor: 'orange-8' },
  { id: 'BEM-004', descricao: 'Monitor Samsung 24"', serie: 'SAM-MON-001', categoria: 'Monitor', marca: 'Samsung • T350', departamento: 'Dep. Administrativo', responsavel: 'Ana Oliveira', status: 'Ativo', icon: 'monitor', avatarColor: 'purple-1', iconColor: 'purple-8' },
  { id: 'BEM-005', descricao: 'Impressora Brother HL', serie: 'BRO-HL-001', categoria: 'Impressora', marca: 'Brother • HL-L2360DW', departamento: 'Dep. Licitações', responsavel: 'Pedro Souza', status: 'Inativo', icon: 'print', avatarColor: 'grey-3', iconColor: 'grey-8' },
  { id: 'BEM-006', descricao: 'Scanner Canon', serie: 'CAN-SCAN-001', categoria: 'Scanner', marca: 'Canon • DR-C240', departamento: 'Controle Interno', responsavel: 'Lucas Almeida', status: 'Ativo', icon: 'document_scanner', avatarColor: 'cyan-1', iconColor: 'cyan-8' },
  { id: 'BEM-007', descricao: 'Computador Dell OptiPlex', serie: 'DEL-OPT-001', categoria: 'PC', marca: 'Dell • OptiPlex 7090', departamento: 'Diretoria Geral', responsavel: 'Fernanda Costa', status: 'Descartado', icon: 'desktop_windows', avatarColor: 'red-1', iconColor: 'red-8' },
  { id: 'BEM-008', descricao: 'Notebook HP ProBook', serie: 'HP-PRO-001', categoria: 'Notebook', marca: 'HP • ProBook 450', departamento: 'Dep. Engenharia', responsavel: 'Ricardo Lima', status: 'Ativo', icon: 'laptop', avatarColor: 'indigo-1', iconColor: 'indigo-8' },
  { id: 'BEM-009', descricao: 'Monitor LG UltraWide', serie: 'LG-ULT-001', categoria: 'Monitor', marca: 'LG • UltraWide 29"', departamento: 'Assessoria Jurídica', responsavel: 'Juliana Martins', status: 'Em Manutenção', icon: 'monitor', avatarColor: 'teal-1', iconColor: 'teal-8' },
])

function receberBens(bensAtualizados) {
  bens.value = bensAtualizados
}

/*
 * BACKEND — SPRING BOOT
 *
 * const response = await api.get('/bens')
 * bens.value = response.data
 *
 * Header e tabela continuam recebendo os mesmos dados via props.
 */
</script>
