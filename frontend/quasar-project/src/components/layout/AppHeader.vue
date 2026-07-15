

<template>
  <q-header elevated class="bg-primary text-white">

    <q-toolbar class="header-toolbar">

      <!-- Botão Sidebar -->
      <q-btn
        flat
        round
        dense
        icon="menu"
        class="header-menu-button"
        @click="$emit('toggle-drawer')"
      />

      <!-- Logo -->
      <AppLogo />

      <q-space />

      <!-- Usuário -->
      <q-btn
        flat
        round
        icon="account_circle"
        class="header-user-button"
      />

    </q-toolbar>

  </q-header>
</template>

<script setup>
  import AppLogo from './AppLogo.vue'

defineEmits([
  'toggle-drawer'
])
</script>