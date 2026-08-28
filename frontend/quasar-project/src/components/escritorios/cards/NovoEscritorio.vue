<template>

  <q-dialog
    v-model="dialog"
    persistent
    transition-show="fade"
    transition-hide="fade"
  >

    <q-card class="escritorio-cadastro-dialog" style="width: 800px; max-width: 90vw; max-height: 120vh">

      <!-- =====================================================
           CABEÇALHO
      ====================================================== -->

      <div class="escritorio-cadastro-header">

        <div class="escritorio-cadastro-titulo-area">

          <div class="escritorio-cadastro-titulo">
            Novo Escritório
          </div>

          <div class="escritorio-cadastro-subtitulo">
            Preencha os dados do escritório.
          </div>

        </div>

        <!-- BOTÃO FECHAR -->

        <q-btn
          flat
          round
          dense
          icon="close"
          class="escritorio-cadastro-fechar"
          @click="fechar"
        />

      </div>


      <q-separator />


      <!-- =====================================================
           CONTEÚDO
      ====================================================== -->

      <div class="escritorio-cadastro-content">

        <div class="escritorio-form">


          <!-- =================================================
               NOME DO ESCRITÓRIO
          ================================================== -->

          <div class="escritorio-form-group">

            <label class="escritorio-form-label">
              Nome do Escritório
              <span class="campo-obrigatorio">*</span>
            </label>

            <q-input
              v-model="form.nome"
              outlined
              dense
              placeholder="Ex: Filial Feira de Santana"
              class="escritorio-input"
            />

          </div>


          <!-- =================================================
               CIDADE + CEP
          ================================================== -->

          <div class="escritorio-form-row">

            <!-- CIDADE -->

            <div class="escritorio-form-group">

              <label class="escritorio-form-label">
                Cidade
                <span class="campo-obrigatorio">*</span>
              </label>

              <q-input
                v-model="form.cidade"
                outlined
                dense
                placeholder="Ex: Feira de Santana"
                class="escritorio-input"
              />

            </div>


            <!-- CEP -->

            <div class="escritorio-form-group">

              <label class="escritorio-form-label">
                CEP
              </label>

              <q-input
                v-model="form.cep"
                outlined
                dense
                placeholder="00000-000"
                class="escritorio-input"
              />

            </div>

          </div>


          <!-- =================================================
               ENDEREÇO
          ================================================== -->

          <div class="escritorio-form-group">

            <label class="escritorio-form-label">
              Endereço
            </label>

            <q-input
              v-model="form.endereco"
              outlined
              dense
              placeholder="Rua, número, bairro"
              class="escritorio-input"
            />

          </div>


          <!-- =================================================
               TELEFONE + E-MAIL
          ================================================== -->

          <div class="escritorio-form-row">

            <!-- TELEFONE -->

            <div class="escritorio-form-group">

              <label class="escritorio-form-label">
                Telefone
              </label>

              <q-input
                v-model="form.telefone"
                outlined
                dense
                placeholder="(71) 3000-0000"
                class="escritorio-input"
              />

            </div>


            <!-- E-MAIL -->

            <div class="escritorio-form-group">

              <label class="escritorio-form-label">
                E-mail
              </label>

              <q-input
                v-model="form.email"
                outlined
                dense
                placeholder="filial@car.ba.gov.br"
                class="escritorio-input"
              />

            </div>

          </div>


          <!-- =================================================
               GESTOR + STATUS
          ================================================== -->

          <div class="escritorio-form-row">

            <!-- GESTOR -->

            <div class="escritorio-form-group">

              <label class="escritorio-form-label">
                Gestor Responsável
              </label>

              <q-input
                v-model="form.gestor"
                outlined
                dense
                placeholder="Nome do responsável"
                class="escritorio-input"
              />

            </div>


            <!-- STATUS -->

            <div class="escritorio-form-group">

              <label class="escritorio-form-label">
                Status
              </label>

              <q-select
                v-model="form.status"
                outlined
                dense
                :options="statusOptions"
                emit-value
                map-options
                class="escritorio-input"
              />

            </div>

          </div>

        </div>

      </div>


      <!-- =====================================================
           RODAPÉ
      ====================================================== -->

      <q-separator />

      <div class="escritorio-cadastro-footer">

        <q-btn
          outline
          color="grey-7"
          label="Cancelar"
          no-caps
          class="escritorio-btn-cancelar"
          @click="fechar"
        />

        <q-btn
          unelevated
          color="positive"
          label="Salvar"
          no-caps
          class="escritorio-btn-salvar"
          @click="salvar"
        />

      </div>

    </q-card>

  </q-dialog>

</template>


<script setup>

import { ref, computed } from 'vue'


/* ==========================================================
   PROPS
========================================================== */

const props = defineProps({

  modelValue: {
    type: Boolean,
    default: false
  }

})


/* ==========================================================
   EMITS
========================================================== */

const emit = defineEmits([
  'update:modelValue',
  'salvo'
])


/* ==========================================================
   CONTROLE DO DIALOG
========================================================== */

/*
 * O Dialog agora utiliza diretamente o
 * modelValue recebido pela página.
 *
 * Quando a página fizer:
 *
 * NovoEscritorios.value = true
 *
 * este computed também será atualizado
 * e o q-dialog será aberto.
 */

const dialog = computed({

  get: () => props.modelValue,

  set: (valor) => {
    emit('update:modelValue', valor)
  }

})


/* ==========================================================
   FORMULÁRIO
========================================================== */

/*
 * MOCK TEMPORÁRIO
 *
 * FUTURO BACKEND:
 *
 * Estes dados deverão ser enviados para:
 *
 * POST /api/escritorios
 */

const form = ref({

  nome: '',

  cidade: '',

  cep: '',

  endereco: '',

  telefone: '',

  email: '',

  gestor: '',

  status: 'Ativo'

})


/* ==========================================================
   STATUS
========================================================== */

const statusOptions = [

  {
    label: 'Ativo',
    value: 'Ativo'
  },

  {
    label: 'Inativo',
    value: 'Inativo'
  }

]


/* ==========================================================
   FECHAR
========================================================== */

function fechar() {

  emit(
    'update:modelValue',
    false
  )

}


/* ==========================================================
   SALVAR
========================================================== */

function salvar() {

  /* ========================================================
     VALIDAÇÃO
  ======================================================== */

  if (!form.value.nome || !form.value.cidade) {

    console.warn(
      'Nome e cidade são obrigatórios.'
    )

    return

  }


  /* ========================================================
     BACKEND
  ========================================================

  FUTURAMENTE:

  const response = await api.post(
    '/escritorios',
    form.value
  )

  ======================================================== */


  console.log(
    'Escritório cadastrado:',
    form.value
  )


  /* ========================================================
     ENVIA O ESCRITÓRIO PARA A PÁGINA
  ======================================================== */

  emit(
    'salvo',
    { ...form.value }
  )


  /* ========================================================
     FECHA O DIALOG
  ======================================================== */

  emit(
    'update:modelValue',
    false
  )


  /* ========================================================
     LIMPA O FORMULÁRIO
  ======================================================== */

  limparFormulario()

}


/* ==========================================================
   LIMPAR FORMULÁRIO
========================================================== */

function limparFormulario() {

  form.value = {

    nome: '',

    cidade: '',

    cep: '',

    endereco: '',

    telefone: '',

    email: '',

    gestor: '',

    status: 'Ativo'

  }

}

</script>