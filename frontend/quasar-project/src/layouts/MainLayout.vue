<script setup>
import { ref, watch } from 'vue'
import { useQuasar } from 'quasar'

import AppHeader from '../components/layout/AppHeader.vue'
import AppSidebar from '../components/layout/AppSidebar.vue'

const $q = useQuasar()

const drawer = ref($q.screen.gt.sm)
const miniState = ref(true)

watch(
  () => $q.screen.gt.sm,
  (desktop) => {
    if (desktop) {
      drawer.value = true
      miniState.value = true
    } else {
      drawer.value = false
      miniState.value = false
    }
  },
  { immediate: true }
)

function toggleDrawer () {
  if ($q.screen.gt.sm) {
    miniState.value = !miniState.value
  } else {
    drawer.value = !drawer.value
  }
}
</script>

<template>
  <q-layout view="hHh Lpr lFf">

    <AppHeader
      @toggle-drawer="toggleDrawer"
    />

    <AppSidebar
      v-model="drawer"
      :mini="miniState"
    />

    <q-page-container>
      <router-view />
    </q-page-container>

  </q-layout>
</template>