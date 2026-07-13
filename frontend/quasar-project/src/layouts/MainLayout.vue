
<script setup>
import AppHeader from '../components/layout/AppHeader.vue'
import AppSidebar from '../components/layout/AppSideBar.vue'
</script>
<template>
  <q-layout view="lHh Lpr lFf">

    <AppHeader />

    <AppSidebar />

    <q-page-container>
      <router-view />
    </q-page-container>

  </q-layout>
</template>

