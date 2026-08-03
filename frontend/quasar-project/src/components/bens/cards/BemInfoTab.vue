<template>

  <div class="bem-info-grid">

    <!-- =====================================================
         COLUNA ESQUERDA
    ====================================================== -->

    <div class="info-column">

      <div class="info-item">
        <q-icon name="tag" class="info-icon" />

        <div>
          <div class="info-label">PATRIMÔNIO</div>
          <div class="info-value">
            {{ bem.patrimonio }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="inventory_2" class="info-icon" />

        <div>
          <div class="info-label">DESCRIÇÃO</div>
          <div class="info-value">
            {{ bem.nome }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="category" class="info-icon" />

        <div>
          <div class="info-label">CATEGORIA</div>
          <div class="info-value">
            {{ bem.categoria }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="business" class="info-icon" />

        <div>
          <div class="info-label">MARCA</div>
          <div class="info-value">
            {{ bem.marca }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon
          name="precision_manufacturing"
          class="info-icon"
        />

        <div>
          <div class="info-label">MODELO</div>
          <div class="info-value">
            {{ bem.modelo }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="qr_code" class="info-icon" />

        <div>
          <div class="info-label">NÚMERO DE SÉRIE</div>
          <div class="info-value">
            {{ bem.numeroSerie }}
          </div>
        </div>
      </div>

    </div>

    <!-- =====================================================
         COLUNA DIREITA
    ====================================================== -->

    <div class="info-column">

      <div class="info-item">
        <q-icon name="event" class="info-icon" />

        <div>
          <div class="info-label">DATA DE AQUISIÇÃO</div>
          <div class="info-value">
            {{ bem.dataAquisicao }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="apartment" class="info-icon" />

        <div>
          <div class="info-label">DEPARTAMENTO</div>
          <div class="info-value">
            {{ bem.departamento }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="person" class="info-icon" />

        <div>
          <div class="info-label">RESPONSÁVEL</div>
          <div class="info-value">
            {{ bem.responsavel }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon name="place" class="info-icon" />

        <div>
          <div class="info-label">LOCALIZAÇÃO</div>
          <div class="info-value">
            {{ bem.localizacao }}
          </div>
        </div>
      </div>

      <div class="info-item">
        <q-icon
          name="check_circle"
          class="info-icon"
        />

        <div>
          <div class="info-label">STATUS</div>

          <div
            class="info-value"
            :class="statusClass(bem.status)"
          >
            {{ bem.status }}
          </div>

        </div>
      </div>

      <div class="info-item align-top">

        <q-icon
          name="description"
          class="info-icon"
        />

        <div>

          <div class="info-label">
            OBSERVAÇÕES
          </div>

          <div class="info-value">
            {{ bem.observacoes }}
          </div>

        </div>

      </div>

    </div>

  </div>

</template>

<script setup>

import { ref } from 'vue'

/* ==========================================================
   BACKEND

   GET /api/bens/{id}

   Quando a API estiver pronta,
   substituir este mock pelos
   dados retornados do backend.

========================================================== */

const bem = ref({

  patrimonio: 'BEM-0041',

  nome: 'Trator Agrícola MF 275',

  categoria: 'Veículo',

  marca: 'Massey Ferguson',

  modelo: 'MF 275',

  numeroSerie: 'MF275-2023-001',

  dataAquisicao: '18/03/2023',

  departamento: 'Operações',

  responsavel: 'Carlos Andrade',

  localizacao: 'Galpão Central',

  status: 'Ativo',

  observacoes:
    'Equipamento utilizado nas operações agrícolas da unidade central.'

})

function statusClass(status) {

  switch (status) {

    case 'Ativo':
      return 'status-ok'

    case 'Em Manutenção':
      return 'status-warning'

    case 'Inativo':
      return 'status-danger'

    case 'Descartado':
      return 'status-neutral'

    default:
      return ''

  }

}

</script>