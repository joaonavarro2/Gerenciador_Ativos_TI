<template>

  <div class="movements-container">

    <div
      v-for="movimento in movimentacoes"
      :key="movimento.id"
      class="movement-card"
    >

      <!-- Linha da timeline -->

      <div class="movement-left">

        <div class="movement-dot"></div>

        <div class="movement-line"></div>

      </div>

      <!-- Conteúdo -->

      <div class="movement-content">

        <div class="movement-header">

          <q-chip
            dense
            square
            class="movement-chip"
          >
            {{ movimento.tipo }}
          </q-chip>

          <span class="movement-date">

            <q-icon
              name="schedule"
              size="15px"
            />

            {{ movimento.data }}

          </span>

        </div>

        <div class="movement-route">

          <div class="movement-origin">

            <q-icon
              name="logout"
              size="18px"
            />

            <strong>Origem</strong>

            {{ movimento.origem }}

          </div>

          <div class="movement-arrow">

            <q-icon
              name="east"
              size="22px"
            />

          </div>

          <div class="movement-destination">

            <q-icon
              name="login"
              size="18px"
            />

            <strong>Destino</strong>

            {{ movimento.destino }}

          </div>

        </div>

        <div class="movement-user">

          <q-icon
            name="person"
            size="17px"
          />

          {{ movimento.usuario }}

        </div>

      </div>

    </div>

  </div>

</template>

<script setup>

/*
======================================================

BACKEND

GET /api/bens/{id}/movimentacoes

======================================================

Resposta esperada

[
  {
    id,
    tipo,
    data,
    origem,
    destino,
    usuario
  }
]

======================================================
*/

const movimentacoes = [

  {
    id:1,
    tipo:'Transferência',
    data:'15/03/2026',
    origem:'Escritório Central',
    destino:'Galpão Operacional',
    usuario:'Carlos Andrade'
  },

  {
    id:2,
    tipo:'Mudança de Responsável',
    data:'02/02/2026',
    origem:'João Pereira',
    destino:'Maria Souza',
    usuario:'Administrador'
  },

  {
    id:3,
    tipo:'Transferência',
    data:'18/11/2025',
    origem:'Almoxarifado',
    destino:'Escritório Central',
    usuario:'Carlos Andrade'
  }

]

</script>