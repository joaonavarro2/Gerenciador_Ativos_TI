<template>
  <q-page class="escritorios-page">
    <EscritoriosHeader v-model:filtros="filtros" />
    <EscritoriosTable
      :escritorios="escritoriosFiltrados"
      :possui-filtros="possuiFiltros"
      :filtros="filtros"
    />
  </q-page>
</template>

<script setup>
import { computed, ref } from 'vue'
import EscritoriosHeader from '@/components/escritorios/EscritoriosHeader.vue'
import EscritoriosTable from '@/components/escritorios/EscritoriosTable.vue'

const escritorios = ref([
  {
    id: 1,
    nome: 'Escritório Regional Salvador',
    cidade: 'Salvador',
    estado: 'BA',
    endereco: 'Av. Tancredo Neves, 1200',
    responsavel: 'Ana Souza',
    status: 'Ativo',
  },
  {
    id: 2,
    nome: 'Escritório de Vitória da Conquista',
    cidade: 'Vitória da Conquista',
    estado: 'BA',
    endereco: 'Rua das Flores, 88',
    responsavel: 'Marcos Lima',
    status: 'Em expansão',
  },
  {
    id: 3,
    nome: 'Escritório de Barreiras',
    cidade: 'Barreiras',
    estado: 'BA',
    endereco: 'Rua 3, 215',
    responsavel: 'Sérgio Almeida',
    status: 'Ativo',
  },
  {
    id: 4,
    nome: 'Escritório de Alagoinhas',
    cidade: 'Alagoinhas',
    estado: 'BA',
    endereco: 'Praça Central, 40',
    responsavel: 'Renata Costa',
    status: 'Inativo',
  },
  {
    id: 5,
    nome: 'Escritório de Aracaju',
    cidade: 'Aracaju',
    estado: 'SE',
    endereco: 'Rua Itabaiana, 310',
    responsavel: 'Rafael dos Santos',
    status: 'Ativo',
  },
  {
    id: 6,
    nome: 'Escritório de Serra',
    cidade: 'Serra',
    estado: 'ES',
    endereco: 'Avenida Carlos Lindenberg, 65',
    responsavel: 'Cláudia Reis',
    status: 'Em expansão',
  },
])

const filtros = ref({
  busca: '',
  estado: null,
  cidade: null,
  status: null,
})

const possuiFiltros = computed(() => Object.values(filtros.value).some(value => value !== null && value !== ''))

function texto(valor) {
  if (valor === null || valor === undefined) return ''
  if (Array.isArray(valor)) return valor.map(item => texto(item)).join(', ')
  if (typeof valor === 'object') return valor.nome || valor.label || valor.value || ''
  return String(valor)
}

function normalizar(valor) {
  return texto(valor).trim().toLowerCase()
}

const escritoriosFiltrados = computed(() => {
  const f = filtros.value

  return escritorios.value.filter(item => {
    if (f.busca) {
      const busca = normalizar(f.busca)
      const combinado = `${item.nome} ${item.cidade} ${item.estado} ${item.responsavel}`
      if (!normalizar(combinado).includes(busca)) return false
    }

    if (f.estado) {
      if (normalizar(item.estado) !== normalizar(f.estado)) return false
    }

    if (f.cidade) {
      if (normalizar(item.cidade) !== normalizar(f.cidade)) return false
    }

    if (f.status) {
      if (normalizar(item.status) !== normalizar(f.status)) return false
    }

    return true
  })
})
</script>

<style scoped>
.escritorios-page {
  padding: 24px;
}
</style>
