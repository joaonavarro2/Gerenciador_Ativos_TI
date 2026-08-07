<!-- eslint-disable vue/multi-word-component-names -->
<template>

  <q-dialog
    :model-value="props.modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="emit('update:modelValue', $event)"
  >

    <q-card class="cadastro-modal" style="
    width:900px;

  max-width:95vw;

  border-radius:18px;

  overflow:hidden;">

      <!-- ======================================================
           CABEÇALHO
      ======================================================= -->

      <q-card-section class="row items-center justify-between q-pb-sm">

        <div class="row items-center">

          <q-avatar
            color="green-1"
            text-color="teal"
            icon="inventory_2"
            size="48px"
          />

          <div class="q-ml-md">

            <div class="text-h5 text-weight-bold">
              Cadastrar Novo Bem
            </div>

            <div class="text-grey-7">
              Registre um novo bem organizacional no sistema de inventário.
            </div>

          </div>

        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          @click="emit('update:modelValue', false)"
        />

      </q-card-section>

      <q-separator />

      <!-- ======================================================
           CONTEÚDO
      ======================================================= -->

      <q-card-section class="scroll formulario-area">

        <!-- IDENTIFICAÇÃO -->

        <div class="titulo-secao">
          IDENTIFICAÇÃO BÁSICA
        </div>

        <div class="row q-col-gutter-md">

          <div class="col-12 col-md-6">

            <q-input
              outlined
              dense
              v-model="form.nome"
              label="Nome do Bem *"
              placeholder="Ex: Trator Agrícola MF 275"
            />

          </div>

          <div class="col-12 col-md-6">

            <q-select
              outlined
              dense
              v-model="form.categoria"
              :options="categorias"
              label="Categoria do Bem *"
            />

          </div>

          <div class="col-12 col-md-6">

            <q-input
              outlined
              dense
              v-model="form.codigo"
              label="Código Interno do Bem *"
              placeholder="BEM-0500"
            />

          </div>

          <div class="col-12 col-md-6">

            <q-input
              outlined
              dense
              v-model="form.patrimonio"
              label="Número de Patrimônio *"
              placeholder="PAT-2024-0112"
            />

          </div>

          <div class="col-12">

            <q-input
              outlined
              dense
              v-model="form.serie"
              label="Número de Série"
              placeholder="SN-AB123456"
            />

          </div>

        </div>

        <!-- FABRICANTE -->

        <div class="titulo-secao q-mt-lg">
          INFORMAÇÕES DO FABRICANTE
        </div>

        <div class="row q-col-gutter-md">

          <div class="col-12 col-md-6">

            <q-input
              outlined
              dense
              v-model="form.fabricante"
              label="Fabricante"
            />

          </div>

          <div class="col-12 col-md-6">

            <q-input
              outlined
              dense
              v-model="form.modelo"
              label="Modelo"
            />

          </div>

        </div>

        <!-- DESCRIÇÃO -->

        <div class="titulo-secao q-mt-lg">
          DETALHES DO ATIVO
        </div>

        <q-input
          outlined
          type="textarea"
          rows="4"
          v-model="form.descricao"
          label="Descrição do Bem"
        />

        <!-- ADMINISTRATIVO -->

        <div class="titulo-secao q-mt-lg">
          INFORMAÇÕES ADMINISTRATIVAS
        </div>

        <div class="row q-col-gutter-md">

          <div class="col-12 col-md-6">

            <q-select
              outlined
              dense
              v-model="form.status"
              :options="statusOptions"
              label="Status do Bem"
            />

          </div>

          <div class="col-12 col-md-6">

            <q-input
              outlined
              dense
              v-model="form.dataAquisicao"
              label="Data de Aquisição"
            >

              <template #append>

                <q-icon
                  name="event"
                  class="cursor-pointer"
                >

                  <q-popup-proxy
                    cover
                    transition-show="scale"
                    transition-hide="scale"
                  >

                    <q-date
                      v-model="form.dataAquisicao"
                      mask="DD/MM/YYYY"
                    >

                      <div class="row justify-end q-pa-sm">

                        <q-btn
                          flat
                          color="primary"
                          label="Fechar"
                          v-close-popup
                        />

                      </div>

                    </q-date>

                  </q-popup-proxy>

                </q-icon>

              </template>

            </q-input>

          </div>

          <div class="col-12 col-md-6">

            <q-select
              outlined
              dense
              v-model="form.escritorio"
              :options="escritorios"
              label="Escritório"
            />

          </div>

          <div class="col-12 col-md-6">

            <q-select
              outlined
              dense
              v-model="form.departamento"
              :options="departamentos"
              label="Departamento"
            />

          </div>

          <div class="col-12">

            <q-select
              outlined
              dense
              use-input
              fill-input
              hide-selected
              v-model="form.responsavel"
              :options="responsaveis"
              label="Funcionário Responsável"
            />

          </div>

        </div>

        <!-- ANEXOS -->

        <div class="titulo-secao q-mt-lg">
          ANEXOS
        </div>

        <q-file
          outlined
          multiple
          use-chips
          v-model="form.anexos"
          label="Clique para selecionar arquivos"
        >

          <template #prepend>

            <q-icon name="upload_file" />

          </template>

        </q-file>

      </q-card-section>

      <q-separator />

      <!-- ======================================================
           RODAPÉ
      ======================================================= -->

      <q-card-actions
        align="right"
        class="q-pa-md"
      >

        <q-btn
          flat
          color="grey-8"
          label="Cancelar"
          @click="emit('update:modelValue', false)"
        />

        <q-btn
          color="positive"
          unelevated
          icon="check_circle"
          label="Cadastrar Bem"
          @click="salvarBem"
        />

      </q-card-actions>

    </q-card>

  </q-dialog>

</template>


<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar()

/* ==========================================================
   PROPS / EMITS
========================================================== */

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits([
  'update:modelValue'
])

/* ==========================================================
   FORMULÁRIO
========================================================== */

const form = ref({

  nome: '',
  categoria: null,

  codigo: '',
  patrimonio: '',
  serie: '',

  fabricante: '',
  modelo: '',

  descricao: '',

  status: null,
  dataAquisicao: '',

  escritorio: null,
  departamento: null,
  responsavel: null,

  anexos: []

})

/* ==========================================================
   MOCKS
========================================================== */

const categorias = [
  'Computadores',
  'Notebooks',
  'Monitores',
  'Impressoras',
  'Veículos',
  'Máquinas Agrícolas',
  'Móveis',
  'Equipamentos Industriais',
  'Ferramentas',
  'Celulares',
  'Outros'
]

const statusOptions = [
  'Ativo',
  'Em Manutenção',
  'Inativo',
  'Descartado'
]

const escritorios = [
  'Matriz',
  'Filial Norte',
  'Filial Sul',
  'Filial Leste',
  'Filial Oeste'
]

const departamentos = [
          'Assessoria da Diretoria',
          'Controle Interno',
          'Comitê Técnico Ambiental',
          'Diretoria Geral',
          'Assessoria de Comunicação',
          'Assessoria Jurídica',
          'Dep.Recursos Humanos',
          'Dep.Administrativo',
          'Dep.Convênios e Contratos',
          'Dep.Financeiro',
          'Dep.Licitações',
          'Dep.Capacitação',
          'Dep.Engenharia',
          'Coord.Água para Todos',
          'Coord.Pró-Semiárido',
          'Coord.Bahia Produtiva',
          'Coord.Projetos Especiais',
          'Coord.Articulação de Políticas',
]

const responsaveis = [
  'Carlos Andrade',
  'Mariana Silva',
  'João Mendes',
  'Ana Paula Costa',
  'Pedro Lima',
  'Fernanda Rocha',
  'Rafael Oliveira'
]

/* ==========================================================
   LIMPAR FORMULÁRIO
========================================================== */

function limparFormulario(){

  form.value={

    nome:'',
    categoria:null,

    codigo:'',
    patrimonio:'',
    serie:'',

    fabricante:'',
    modelo:'',

    descricao:'',

    status:null,
    dataAquisicao:'',

    escritorio:null,
    departamento:null,
    responsavel:null,

    anexos:[]

  }

}

/* ==========================================================
   SALVAR
========================================================== */

function salvarBem(){

  if(

    !form.value.nome ||
    !form.value.categoria ||
    !form.value.codigo ||
    !form.value.patrimonio

  ){

    $q.notify({

      type:'negative',

      message:'Preencha os campos obrigatórios.'

    })

    return

  }

  /*
  ========================================

  BACKEND

  POST /api/bens

  Enviar o objeto "form"

  Após sucesso:

  - atualizar tabela

  - fechar modal

  - limpar formulário

  ========================================
  */

  console.log(form.value)

  $q.notify({

    type:'positive',

    message:'Bem cadastrado com sucesso.'

  })

  emit('update:modelValue',false)

  limparFormulario()

}
</script>