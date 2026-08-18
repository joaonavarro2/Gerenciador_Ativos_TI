<!-- eslint-disable vue/multi-word-component-names -->

<template>
  <q-dialog
    :model-value="props.modelValue"
    persistent
    transition-show="fade"
    transition-hide="fade"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <q-card
      class="cadastro-modal"
      style="
        width: 900px;
        max-width: 95vw;
        border-radius: 18px;
        overflow: hidden;
      "
    >

      <!-- =====================================================
           CABEÇALHO
      ====================================================== -->

      <q-card-section class="row items-center justify-between q-pb-sm">

        <div class="row items-center">

          <q-avatar
            color="green-1"
            text-color="teal"
            :icon="modoEdicao ? 'edit' : 'inventory_2'"
            size="48px"
          />

          <div class="q-ml-md">

            <div class="text-h5 text-weight-bold">
              {{ modoEdicao ? 'Editar Bem' : 'Cadastrar Novo Bem' }}
            </div>

            <div class="text-grey-7">
              {{
                modoEdicao
                  ? 'Altere as informações do bem selecionado.'
                  : 'Registre um novo bem organizacional no sistema de inventário.'
              }}
            </div>

          </div>

        </div>

        <q-btn
          flat
          round
          dense
          icon="close"
          @click="fecharFormulario"
        />

      </q-card-section>

      <q-separator />


      <!-- =====================================================
           CONTEÚDO
      ====================================================== -->

      <q-card-section class="scroll formulario-area">


        <!-- =====================================================
             IDENTIFICAÇÃO
        ====================================================== -->

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
              placeholder="Ex: Computador Dell"
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


        <!-- =====================================================
             FABRICANTE
        ====================================================== -->

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


        <!-- =====================================================
             DESCRIÇÃO
        ====================================================== -->

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


        <!-- =====================================================
             ADMINISTRATIVO
        ====================================================== -->

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


        <!-- =====================================================
             ANEXOS
        ====================================================== -->

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


      <!-- =====================================================
           RODAPÉ
      ====================================================== -->

      <q-card-actions
        align="right"
        class="q-pa-md"
      >

        <q-btn
          flat
          color="grey-8"
          label="Cancelar"
          @click="fecharFormulario"
        />

        <q-btn
          color="positive"
          unelevated
          :icon="modoEdicao ? 'save' : 'check_circle'"
          :label="modoEdicao ? 'Salvar Alterações' : 'Cadastrar Bem'"
          @click="salvarBem"
        />

      </q-card-actions>

    </q-card>

  </q-dialog>
</template>


<script setup>

import {
  ref,
  computed,
  watch
} from 'vue'

import { useQuasar } from 'quasar'

const $q = useQuasar()


/* ==========================================================
   PROPS / EMITS
========================================================== */

const props = defineProps({

  /*
   * Controla abertura do modal.
   */
  modelValue: {
    type: Boolean,
    default: false
  },

  /*
   * Bem que será editado.
   *
   * Quando for null:
   * → cadastro
   *
   * Quando possuir um objeto:
   * → edição
   */
  bem: {
    type: Object,
    default: null
  }

})

const emit = defineEmits([
  'update:modelValue',
  'salvo'
])


/* ==========================================================
   MODO
========================================================== */

const modoEdicao = computed(() => {

  return !!props.bem

})


/* ==========================================================
   FORMULÁRIO
========================================================== */

const form = ref({

  id: null,

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
   MOcks
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

  'Alagoinhas',
  'Amargosa',
  'Barreiras',
  'Bom Jesus da Lapa',
  'Caetité',
  'Salvador',
  'Cruz das Almas',
  'Eunápolis',
  'Ribeira do Pombal',
  'Feira de Santana',
  'Irecê',
  'Itaberaba',
  'Itabuna',
  'Itapetinga',
  'Jacobina',
  'Jequié',
  'Juazeiro',
  'Macaúbas',
  'Paulo Afonso',
  'Riachão do Jacuípe',
  'Santa Maria da Vitória',
  'Seabra',
  'Senhor do Bonfim',
  'Serrinha',
  'Teixeira de Freitas',
  'Valença',
  'Vitória da Conquista'

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
  'Coord.Articulação de Políticas'

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

function limparFormulario() {

  form.value = {

    id: null,

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

  }

}


/* ==========================================================
   CARREGAR BEM PARA EDIÇÃO
========================================================== */

/*
 * Quando o usuário clicar no botão de editar,
 * o componente pai enviará o objeto do bem através
 * da prop "bem".
 *
 * Exemplo:
 *
 * bem = {
 *   id: 15,
 *   nome: 'Notebook Dell',
 *   categoria: 'Notebooks',
 *   ...
 * }
 *
 * O formulário será preenchido automaticamente.
 */

function carregarBem(bem) {

  if (!bem) {

    limparFormulario()

    return

  }

  form.value = {

    id: bem.id ?? null,

    nome: bem.nome ?? bem.nomeItem ?? '',

    categoria:
      bem.categoria ??
      bem.categoriaBem ??
      bem.tipo ??
      null,

    codigo:
      bem.codigo ??
      bem.codigoInterno ??
      '',

    patrimonio:
      bem.patrimonio ??
      '',

    serie:
      bem.serie ??
      bem.serial ??
      bem.numeroSerie ??
      '',

    fabricante:
      bem.fabricante ??
      bem.marca ??
      '',

    modelo:
      bem.modelo ??
      '',

    descricao:
      bem.descricao ??
      bem.descricaoBem ??
      '',

    status:
      bem.status ??
      null,

    dataAquisicao:
      bem.dataAquisicao ??
      '',

    escritorio:
      bem.escritorio ??
      null,

    departamento:
      bem.departamento ??
      bem.nomeDepartamento ??
      null,

    responsavel:
      bem.responsavel ??
      bem.nomeResponsavel ??
      null,

    anexos:
      bem.anexos ??
      []

  }

}


/* ==========================================================
   WATCH
========================================================== */

/*
 * Observa o bem recebido pelo componente.
 *
 * Se o usuário clicar em editar:
 *
 * bem muda
 * ↓
 * carregarBem()
 * ↓
 * formulário é preenchido
 */

watch(

  () => props.bem,

  novoBem => {

    carregarBem(novoBem)

  },

  {
    immediate: true
  }

)


/* ==========================================================
   FECHAR
========================================================== */

function fecharFormulario() {

  emit(
    'update:modelValue',
    false
  )

}


/* ==========================================================
   SALVAR
========================================================== */

function salvarBem() {

  /* ========================================================
     VALIDAÇÃO
  ======================================================== */

  if (

    !form.value.nome ||

    !form.value.categoria ||

    !form.value.codigo ||

    !form.value.patrimonio

  ) {

    $q.notify({

      type: 'negative',

      message:
        'Preencha os campos obrigatórios.'

    })

    return

  }


  /* ========================================================
     CADASTRO
  ======================================================== */

  if (!modoEdicao.value) {

    /*
     * ================================================
     * BACKEND — CADASTRO
     * ================================================
     *
     * Futuramente:
     *
     * POST /api/bens
     *
     * Body:
     *
     * {
     *   nome: form.value.nome,
     *   categoria: form.value.categoria,
     *   codigo: form.value.codigo,
     *   patrimonio: form.value.patrimonio,
     *   ...
     * }
     *
     * O Axios fará:
     *
     * await api.post('/bens', form.value)
     *
     * ================================================
     */

    console.log(
      'MOCK — Cadastro:',
      form.value
    )

    $q.notify({

      type: 'positive',

      message:
        'Bem cadastrado com sucesso.'

    })

  }


  /* ========================================================
     EDIÇÃO
  ======================================================== */

  else {

    /*
     * ================================================
     * BACKEND — EDIÇÃO
     * ================================================
     *
     * Futuramente:
     *
     * PUT /api/bens/{id}
     *
     * Exemplo:
     *
     * await api.put(
     *
     *   `/bens/${form.value.id}`,
     *
     *   form.value
     *
     * )
     *
     * ================================================
     */

    console.log(
      'MOCK — Edição:',
      form.value
    )

    $q.notify({

      type: 'positive',

      message:
        'Bem atualizado com sucesso.'

    })

  }


  /* ========================================================
     AVISAR COMPONENTE PAI
  ======================================================== */

  emit(
    'salvo',
    { ...form.value }
  )


  /* ========================================================
     FECHAR
  ======================================================== */

  fecharFormulario()

  limparFormulario()

}

</script>