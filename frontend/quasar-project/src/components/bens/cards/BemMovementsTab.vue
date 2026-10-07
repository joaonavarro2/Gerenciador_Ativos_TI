<template>
  <div class="movements-container">
    <div v-for="movimento in movimentacoes" :key="movimento.id" class="movement-card">
      <!-- Linha da timeline -->

      <div class="movement-left">
        <div class="movement-dot"></div>

        <div class="movement-line"></div>
      </div>

      <!-- Conteúdo -->

      <div class="movement-content">
        <div class="movement-header">
          <q-chip dense square class="movement-chip">
            {{ movimento.tipo }}
          </q-chip>

          <span class="movement-date">
            <q-icon name="schedule" size="15px" />

            {{ movimento.data }}
          </span>
        </div>

        <div class="movement-route">
          <div class="movement-origin">
            <q-icon name="logout" size="18px" />

            <strong>Origem</strong>

            {{ movimento.origem }}
          </div>

          <div class="movement-arrow">
            <q-icon name="east" size="22px" />
          </div>

          <div class="movement-destination">
            <q-icon name="login" size="18px" />

            <strong>Destino</strong>

            {{ movimento.destino }}
          </div>
        </div>

        <div class="movement-user">
          <q-icon name="person" size="17px" />

          {{ movimento.usuario }}
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

const movimentacoes = ref([])

async function carregarMovimentacoes() {
  if (!props.bemId) return
  const { data } = await api.get(`/bens/${props.bemId}/movimentacoes`)
  movimentacoes.value = data.map((item) => ({
    ...item,
    data: new Date(item.data).toLocaleString('pt-BR'),
  }))
}

onMounted(carregarMovimentacoes)
watch(() => props.bemId, carregarMovimentacoes)
</script>
