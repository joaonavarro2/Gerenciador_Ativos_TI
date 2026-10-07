<template>
  <div class="maintenance-container">
    <div v-for="item in manutencoes" :key="item.id" class="maintenance-card">
      <div class="maintenance-left">
        <div class="maintenance-dot" :class="item.tipo.toLowerCase()" />
      </div>

      <div class="maintenance-content">
        <div class="maintenance-header">
          <q-chip dense square :class="'maintenance-chip ' + item.tipo.toLowerCase()">
            {{ item.tipo }}
          </q-chip>

          <span class="maintenance-date">
            <q-icon name="event" size="15px" />

            {{ item.data }}
          </span>
        </div>

        <div class="maintenance-user">
          <q-icon name="person" size="17px" />

          {{ item.usuario }}
        </div>

        <div class="maintenance-description">
          {{ item.descricao }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { api } from '@/boot/axios'

const props = defineProps({
  bemId: {
    type: Number,
    default: null,
  },
})

const manutencoes = ref([])

async function carregarManutencoes() {
  if (!props.bemId) return
  const { data } = await api.get(`/bens/${props.bemId}/manutencoes`)
  manutencoes.value = data.map((item) => ({
    ...item,
    data: new Date(item.data).toLocaleDateString('pt-BR'),
  }))
}

onMounted(carregarManutencoes)
watch(() => props.bemId, carregarManutencoes)
</script>
