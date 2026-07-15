<template>
  <q-layout view="hHh Lpr lFf">

    <AppHeader
      @toggle-drawer="toggleSidebar"
    />

    <AppSidebar
      v-model="isSidebarExpanded"
    />

    <q-page-container>
      <router-view />
    </q-page-container>

  </q-layout>
</template>

<script setup>
import { ref } from 'vue'

import AppHeader from '../components/layout/AppHeader.vue'
import AppSidebar from '../components/layout/AppSidebar.vue'

const isSidebarExpanded = ref(false)

function toggleSidebar() {
  isSidebarExpanded.value = !isSidebarExpanded.value
}
</script>