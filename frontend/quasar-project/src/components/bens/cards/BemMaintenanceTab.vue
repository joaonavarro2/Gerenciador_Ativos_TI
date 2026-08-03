<template>

  <div class="maintenance-container">

    <div
      v-for="item in manutencoes"
      :key="item.id"
      class="maintenance-card"
    >

      <div class="maintenance-left">

        <div
          class="maintenance-dot"
          :class="item.tipo.toLowerCase()"
        />

      </div>

      <div class="maintenance-content">

        <div class="maintenance-header">

          <q-chip
            dense
            square
            :class="'maintenance-chip ' + item.tipo.toLowerCase()"
          >
            {{ item.tipo }}
          </q-chip>

          <span class="maintenance-date">

            <q-icon
              name="event"
              size="15px"
            />

            {{ item.data }}

          </span>

        </div>

        <div class="maintenance-user">

          <q-icon
            name="person"
            size="17px"
          />

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

/*
======================================================

BACKEND

GET /api/bens/{id}/manutencoes

======================================================
*/

const manutencoes = [

  {
    id:1,
    tipo:'Preventiva',
    data:'15/03/2026',
    usuario:'João da Silva',
    descricao:'Troca de óleo, limpeza geral e inspeção completa do equipamento.'
  },

  {
    id:2,
    tipo:'Corretiva',
    data:'08/01/2026',
    usuario:'Maria Oliveira',
    descricao:'Substituição da bateria e reparo no sistema elétrico.'
  },

  {
    id:3,
    tipo:'Preventiva',
    data:'20/10/2025',
    usuario:'Carlos Andrade',
    descricao:'Lubrificação das peças móveis e reaperto estrutural.'
  }

]

</script>