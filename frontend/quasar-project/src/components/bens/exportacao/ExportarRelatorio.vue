
<template>
<q-dialog v-model="dialogExportar" >
  <q-card class="exportar-dialog">

    <!-- =====================================================
         HEADER
    ====================================================== -->

    <q-card-section class="exportar-header">

      <div class="exportar-header-left">

        <q-avatar
          class="exportar-header-avatar"
          color="blue-1"
          text-color="primary"
          icon="download"
          size="50px"
        />

        <div class="exportar-header-info">

          <div class="exportar-title">
            Exportar Relatório
          </div>

          <div class="exportar-subtitle">
            Gere um relatório completo utilizando os filtros atualmente aplicados.
          </div>

        </div>

      </div>


      <q-btn
        flat
        round
        dense
        icon="close"
        class="exportar-close"
        @click="dialogExportar = false"
      />

    </q-card-section>


    <q-separator />


    <!-- =====================================================
         CONTEÚDO
    ====================================================== -->

    <q-card-section class="exportar-content">


      <!-- ===================================================
           FORMATO
      ==================================================== -->

      <div class="titulo-secao">
        FORMATO DO RELATÓRIO
      </div>


      <q-option-group
        v-model="formato"
        :options="formatos"
        color="primary"
      />


      <!-- ===================================================
           O QUE EXPORTAR
      ==================================================== -->

      <div class="titulo-secao q-mt-lg">
        DADOS
      </div>


      <q-option-group
        v-model="modoExportacao"
        :options="opcoesExportacao"
        color="primary"
      />


      <!-- ===================================================
           FILTROS
      ==================================================== -->

      <div class="titulo-secao q-mt-lg">
        FILTROS UTILIZADOS
      </div>


      <q-list
        bordered
        separator
        class="rounded-borders"
      >


        <!-- =================================================
             PESQUISA
        ================================================== -->

        <q-item>

          <q-item-section avatar>

            <q-icon
              name="search"
              :color="filtroBuscaAplicado ? 'primary' : 'grey-6'"
            />

          </q-item-section>


          <q-item-section>

            <q-item-label>
              Pesquisa
            </q-item-label>


            <q-item-label caption>

              {{ texto(props.filtros.busca) || 'Nenhuma pesquisa aplicada' }}

            </q-item-label>

          </q-item-section>


          <q-item-section side>

            <q-badge
              v-if="filtroBuscaAplicado"
              color="primary"
              label="Aplicado"
            />

          </q-item-section>

        </q-item>


        <!-- =================================================
             CATEGORIA
        ================================================== -->

        <q-item>

          <q-item-section avatar>

            <q-icon
              name="category"
              :color="filtroCategoriaAplicado ? 'primary' : 'grey-6'"
            />

          </q-item-section>


          <q-item-section>

            <q-item-label>
              Categoria
            </q-item-label>


            <q-item-label caption>

              {{ mostrarFiltro(props.filtros.categoria, 'Todas') }}

            </q-item-label>

          </q-item-section>


          <q-item-section side>

            <q-badge
              v-if="filtroCategoriaAplicado"
              color="primary"
              label="Aplicado"
            />

          </q-item-section>

        </q-item>


        <!-- =================================================
             STATUS
        ================================================== -->

        <q-item>

          <q-item-section avatar>

            <q-icon
              name="verified"
              :color="filtroStatusAplicado ? 'primary' : 'grey-6'"
            />

          </q-item-section>


          <q-item-section>

            <q-item-label>
              Status
            </q-item-label>


            <q-item-label caption>

              {{ mostrarFiltro(props.filtros.status, 'Todos') }}

            </q-item-label>

          </q-item-section>


          <q-item-section side>

            <q-badge
              v-if="filtroStatusAplicado"
              color="primary"
              label="Aplicado"
            />

          </q-item-section>

        </q-item>


        <!-- =================================================
             DEPARTAMENTO
        ================================================== -->

        <q-item>

          <q-item-section avatar>

            <q-icon
              name="apartment"
              :color="filtroDepartamentoAplicado ? 'primary' : 'grey-6'"
            />

          </q-item-section>


          <q-item-section>

            <q-item-label>
              Departamento
            </q-item-label>


            <q-item-label caption>

              {{ mostrarFiltro(props.filtros.departamento, 'Todos') }}

            </q-item-label>

          </q-item-section>


          <q-item-section side>

            <q-badge
              v-if="filtroDepartamentoAplicado"
              color="primary"
              label="Aplicado"
            />

          </q-item-section>

        </q-item>

      </q-list>


      <!-- ===================================================
           RESUMO
      ==================================================== -->

      <div class="titulo-secao q-mt-lg">
        RESUMO
      </div>


      <q-banner
        rounded
        class="bg-blue-1 text-primary"
      >

        <div class="text-subtitle1 text-weight-bold">

          {{ quantidadeItens }}

          {{ quantidadeItens === 1 ? 'bem' : 'bens' }}

          serão exportados.

        </div>


        <div class="text-caption">

          <template v-if="modoExportacao === 'todos'">

            O relatório conterá todos os bens cadastrados.

          </template>


          <template v-else-if="possuiFiltros">

            O relatório conterá somente os bens encontrados
            após a aplicação dos filtros selecionados.

          </template>


          <template v-else>

            Nenhum filtro foi aplicado.
            Todos os bens disponíveis serão considerados.

          </template>

        </div>

      </q-banner>

    </q-card-section>


    <q-separator />


    <!-- =====================================================
         RODAPÉ
    ====================================================== -->

    <q-card-actions class="exportar-footer">

      <q-btn
        flat
        color="grey-8"
        label="Cancelar"
        @click="dialogExportar = false"
      />


      <q-btn
        color="primary"
        icon="download"
        label="Exportar"
        :loading="exportando"
        :disable="quantidadeItens === 0"
        @click="exportarRelatorio"
      />

    </q-card-actions>

  </q-card>
</q-dialog>
</template>


<script setup>

import { ref, computed } from 'vue'
import { useQuasar } from 'quasar'

import jsPDF from 'jspdf'
import { autoTable } from 'jspdf-autotable'
import * as XLSX from 'xlsx'


const $q = useQuasar()


/* ======================================================
   PROPS
====================================================== */

const props = defineProps({

  modelValue: Boolean,


  itens: {

    type: Array,

    default: () => []

  },


  filtros: {

    type: Object,

    default: () => ({

      busca: '',

      categoria: [],

      status: [],

      departamento: []

    })

  }

})


const emit = defineEmits([
  'update:modelValue'
])


/* ======================================================
   DIALOG
====================================================== */

const dialogExportar = computed({

  get: () => props.modelValue,

  set: value => {

    emit(
      'update:modelValue',
      value
    )

  }

})


/* ======================================================
   ESTADO DA EXPORTAÇÃO
====================================================== */

const exportando = ref(false)


/* ======================================================
   FORMATO
====================================================== */

const formato = ref('pdf')


const formatos = [

  {
    label: 'PDF (.pdf)',
    value: 'pdf'
  },

  {
    label: 'Excel (.xlsx)',
    value: 'excel'
  },

  {
    label: 'CSV (.csv)',
    value: 'csv'
  }

]


/* ======================================================
   MODO DE EXPORTAÇÃO
====================================================== */

const modoExportacao = ref('filtrados')


const opcoesExportacao = [

  {
    label: 'Exportar somente os itens filtrados',
    value: 'filtrados'
  },

  {
    label: 'Exportar todos os bens cadastrados',
    value: 'todos'
  }

]


/* ======================================================
   MOCKS
====================================================== */

/*
 * Estes mocks servem como fallback enquanto
 * o backend ainda não estiver conectado.
 *
 * Quando a API estiver funcionando, a página
 * poderá simplesmente enviar os dados através
 * da prop "itens".
 */




/* ======================================================
   FONTE DOS DADOS
====================================================== */

/*
 * Se a página já enviar os bens pela prop,
 * usamos os dados recebidos.
 *
 * Se ainda não enviar nada,
 * usamos os mocks.
 */

const itensDisponiveis = computed(() => {

  if (props.itens.length > 0) {

    return props.itens

  }

  return props.itens || [] 

})


/* ======================================================
   FUNÇÃO AUXILIAR
====================================================== */

function texto(valor) {

  if (
    valor === null ||
    valor === undefined
  ) {

    return ''

  }


  if (
    Array.isArray(valor)
  ) {

    return valor
      .map(item => texto(item))
      .filter(Boolean)
      .join(', ')

  }


  if (
    typeof valor === 'object'
  ) {

    return (

      valor.nome ||

      valor.label ||

      valor.name ||

      valor.value ||

      ''

    )

  }


  return String(valor)

}


/* ======================================================
   NORMALIZA FILTRO
====================================================== */

function normalizarFiltro(valor) {

  if (
    valor === null ||
    valor === undefined
  ) {

    return []

  }


  if (
    Array.isArray(valor)
  ) {

    return valor
      .map(item => texto(item).trim().toLowerCase())
      .filter(Boolean)

  }


  const valorTexto =
    texto(valor)
      .trim()
      .toLowerCase()


  if (!valorTexto) {

    return []

  }


  return [
    valorTexto
  ]

}


/* ======================================================
   FILTRO DE PESQUISA
====================================================== */

const filtroBuscaAplicado = computed(() => {

  return Boolean(

    texto(
      props.filtros?.busca
    )
      .trim()

  )

})


/* ======================================================
   FILTRO DE CATEGORIA
====================================================== */

const filtroCategoriaAplicado = computed(() => {

  return (

    normalizarFiltro(
      props.filtros?.categoria
    ).length > 0

  )

})


/* ======================================================
   FILTRO DE STATUS
====================================================== */

const filtroStatusAplicado = computed(() => {

  return (

    normalizarFiltro(
      props.filtros?.status
    ).length > 0

  )

})


/* ======================================================
   FILTRO DE DEPARTAMENTO
====================================================== */

const filtroDepartamentoAplicado = computed(() => {

  return (

    normalizarFiltro(
      props.filtros?.departamento
    ).length > 0

  )

})


/* ======================================================
   POSSUI FILTROS
====================================================== */

const possuiFiltros = computed(() => {

  return (

    filtroBuscaAplicado.value ||

    filtroCategoriaAplicado.value ||

    filtroStatusAplicado.value ||

    filtroDepartamentoAplicado.value

  )

})


/* ======================================================
   MOSTRAR FILTRO NO TEMPLATE
====================================================== */

function mostrarFiltro(valor, vazio) {

  const lista =
    normalizarFiltro(valor)


  if (!lista.length) {

    return vazio

  }


  return lista.join(', ')

}


/* ======================================================
   FILTRAGEM DOS BENS
====================================================== */

const itensFiltrados = computed(() => {

  let resultado = [
    ...itensDisponiveis.value
  ]


  /* ====================================================
     PESQUISA
  ==================================================== */

  const busca =
    texto(
      props.filtros?.busca
    )
      .trim()
      .toLowerCase()


  if (busca) {

    resultado =
      resultado.filter(item => {

        const valoresBusca = [

          item.id,

          item.descricao,

          item.categoria,

          item.marca,

          item.departamento,

          item.responsavel,

          item.status,
        ]


        return valoresBusca.some(valor => {

          return texto(valor)
            .toLowerCase()
            .includes(busca)

        })

      })

  }


  /* ====================================================
     CATEGORIA
  ==================================================== */

  const categorias =
    normalizarFiltro(
      props.filtros?.categoria
    )


  if (categorias.length) {

    resultado =
      resultado.filter(item => {

        const valorCategoria =

          item.categoria ||

          item.categoriaBem ||

          item.tipo


        const categoriaItem =
          texto(valorCategoria)
            .trim()
            .toLowerCase()


        return categorias.includes(
          categoriaItem
        )

      })

  }


  /* ====================================================
     STATUS
  ==================================================== */

  const status =
    normalizarFiltro(
      props.filtros?.status
    )


  if (status.length) {

    resultado =
      resultado.filter(item => {

        const statusItem =
          texto(item.status)
            .trim()
            .toLowerCase()


        return status.includes(
          statusItem
        )

      })

  }


  /* ====================================================
     DEPARTAMENTO
  ==================================================== */

  const departamentos =
    normalizarFiltro(
      props.filtros?.departamento
    )


  if (departamentos.length) {

    resultado =
      resultado.filter(item => {

        const valorDepartamento =

          item.departamento ||

          item.nomeDepartamento ||

          item.idDepartamento


        const departamentoItem =
          texto(valorDepartamento)
            .trim()
            .toLowerCase()


        return departamentos.includes(
          departamentoItem
        )

      })

  }


  return resultado

})


/* ======================================================
   ITENS PARA EXPORTAÇÃO
====================================================== */

const itensParaExportar = computed(() => {

  if (
    modoExportacao.value === 'todos'
  ) {

    return itensDisponiveis.value

  }


  return itensFiltrados.value

})


/* ======================================================
   QUANTIDADE
====================================================== */

const quantidadeItens = computed(() => {

  return itensParaExportar.value.length

})


/* ======================================================
   PREPARAR DADOS DO RELATÓRIO
====================================================== */

function prepararDadosRelatorio() {

  return itensParaExportar.value.map(item => {

    return {

      ID: texto(item.id),

      Nome:
        texto(
          item.descricao 
        ),

      Categoria:
        texto(
          item.categoria ||
          item.categoriaBem ||
          item.tipo
        ),


      Fabricante:
        texto(
          item.fabricante ||
          item.marca
        ),


      Status:
        texto(
          item.status
        ),

      Departamento:
        texto(
          item.departamento ||
          item.nomeDepartamento ||
          item.idDepartamento
        )

    }

  })

}


/* ======================================================
   DATA DO ARQUIVO
====================================================== */

function dataArquivo() {

  const agora =
    new Date()


  const ano =
    agora.getFullYear()


  const mes =
    String(
      agora.getMonth() + 1
    ).padStart(
      2,
      '0'
    )


  const dia =
    String(
      agora.getDate()
    ).padStart(
      2,
      '0'
    )


  return `${ano}-${mes}-${dia}`

}


/* ======================================================
   DATA E HORA
====================================================== */

function dataHoraAtual() {

  return new Date()
    .toLocaleString(
      'pt-BR'
    )

}


/* ======================================================
   DOWNLOAD
====================================================== */

function baixarArquivo(
  blob,
  nomeArquivo
) {

  const url =
    window.URL.createObjectURL(
      blob
    )


  const link =
    document.createElement(
      'a'
    )


  link.href =
    url


  link.download =
    nomeArquivo


  link.style.display =
    'none'


  document.body.appendChild(
    link
  )


  link.click()


  document.body.removeChild(
    link
  )


  setTimeout(() => {

    window.URL.revokeObjectURL(
      url
    )

  }, 1000)

}


/* ======================================================
   PDF
====================================================== */

function exportarPDF() {

  const dados =
    prepararDadosRelatorio()


  if (!dados.length) {

    $q.notify({

      type: 'warning',

      message:
        'Não existem bens para exportar.'

    })

    return false

  }


  try {

    /* ==================================================
       DOCUMENTO
    ================================================== */

    const pdf =
      new jsPDF({

        orientation:
          'landscape',

        unit:
          'mm',

        format:
          'a4'

      })


    /* ==================================================
       CABEÇALHO
    ================================================== */

    pdf.setFont(
      'helvetica',
      'bold'
    )


    pdf.setFontSize(
      18
    )


    pdf.text(
      'Relatório de Bens',
      14,
      18
    )


    pdf.setFont(
      'helvetica',
      'normal'
    )


    pdf.setFontSize(
      9
    )


    pdf.text(

      `Gerado em: ${dataHoraAtual()}`,

      14,

      25

    )


    pdf.text(

      `Quantidade de bens: ${dados.length}`,

      14,

      31

    )


    /* ==================================================
       FILTROS
    ================================================== */

    let posicaoY =
      39


    pdf.setFont(
      'helvetica',
      'bold'
    )


    pdf.text(
      'Filtros utilizados:',
      14,
      posicaoY
    )


    posicaoY += 6


    pdf.setFont(
      'helvetica',
      'normal'
    )


    const filtrosTexto = [

      `Pesquisa: ${
        texto(props.filtros?.busca) ||
        'Não aplicada'
      }`,

      `Categoria: ${
        mostrarFiltro(
          props.filtros?.categoria,
          'Todas'
        )
      }`,

      `Status: ${
        mostrarFiltro(
          props.filtros?.status,
          'Todos'
        )
      }`,

      `Departamento: ${
        mostrarFiltro(
          props.filtros?.departamento,
          'Todos'
        )
      }`

    ]


    /*
     * Divide o texto em duas linhas
     * para evitar ultrapassar a largura.
     */

    const filtrosLinha1 = [

      filtrosTexto[0],

      filtrosTexto[1]

    ].join(' | ')


    const filtrosLinha2 = [

      filtrosTexto[2],

      filtrosTexto[3]

    ].join(' | ')


    pdf.text(
      filtrosLinha1,
      14,
      posicaoY
    )


    posicaoY += 5


    pdf.text(
      filtrosLinha2,
      14,
      posicaoY
    )


    posicaoY += 8


    /* ==================================================
       MODO DE EXPORTAÇÃO
    ================================================== */

    pdf.setFont(
      'helvetica',
      'bold'
    )


    pdf.text(
      'Modo de exportação:',
      14,
      posicaoY
    )


    pdf.setFont(
      'helvetica',
      'normal'
    )


    pdf.text(

      modoExportacao.value === 'todos'
        ? 'Todos os bens cadastrados'
        : 'Somente os bens filtrados',

      48,

      posicaoY

    )


    posicaoY += 8


    /* ==================================================
       TABELA
    ================================================== */

    const colunas =
      Object.keys(
        dados[0]
      )


    const linhas =
      dados.map(item => {

        return colunas.map(
          coluna =>
            texto(
              item[coluna]
            )
        )

      })


    autoTable(

      pdf,

      {

        startY:
          posicaoY,

        head: [
          colunas
        ],

        body:
          linhas,

        theme:
          'grid',

        styles: {

          fontSize:
            7,

          cellPadding:
            2,

          overflow:
            'linebreak'

        },

        headStyles: {

          fontSize:
            7,

          fontStyle:
            'bold'

        },

        alternateRowStyles: {

          fillColor: [
            245,
            247,
            250
          ]

        },

        margin: {

          left:
            14,

          right:
            14

        }

      }

    )


    /* ==================================================
       RODAPÉ
    ================================================== */

    const numeroPaginas =
      pdf.internal.getNumberOfPages()


    for (

      let pagina = 1;

      pagina <= numeroPaginas;

      pagina++

    ) {

      pdf.setPage(
        pagina
      )


      pdf.setFont(
        'helvetica',
        'normal'
      )


      pdf.setFontSize(
        8
      )


      pdf.text(

        `Página ${pagina} de ${numeroPaginas}`,

        14,

        pdf.internal.pageSize.height - 8

      )

    }


    /* ==================================================
       DOWNLOAD
    ================================================== */

    const blob =
      pdf.output(
        'blob'
      )


    baixarArquivo(

      blob,

      `relatorio-bens-${dataArquivo()}.pdf`

    )


    $q.notify({

      type: 'positive',

      message:
        'PDF exportado com sucesso.'

    })


    return true

  } catch (erro) {

    console.error(
      'Erro ao gerar PDF:',
      erro
    )


    $q.notify({

      type: 'negative',

      message:
        'Não foi possível gerar o PDF.'

    })


    return false

  }

}


/* ======================================================
   EXCEL
====================================================== */

/*
 * Mantido no componente para preservar
 * a opção existente.
 *
 * A implementação definitiva poderá ser
 * adicionada posteriormente.
 */

function exportarExcel() {

  const dados = prepararDadosRelatorio()

  if (!dados.length) {

    $q.notify({
      type: 'warning',
      message: 'Não existem bens para exportar.'
    })

    return false

  }

  try {

    const worksheet =
      XLSX.utils.json_to_sheet(dados)

    const workbook =
      XLSX.utils.book_new()

    XLSX.utils.book_append_sheet(
      workbook,
      worksheet,
      'Bens'
    )

    XLSX.writeFile(
      workbook,
      `relatorio-bens-${dataArquivo()}.xlsx`
    )

    $q.notify({
      type: 'positive',
      message: 'Excel exportado com sucesso.'
    })

    return true

  } catch (erro) {

    console.error(
      'Erro ao exportar Excel:',
      erro
    )

    $q.notify({
      type: 'negative',
      message: 'Não foi possível gerar o Excel.'
    })

    return false

  }

}


/* ======================================================
   CSV
====================================================== */

/*
 * Mantido no componente para preservar
 * a opção existente.
 */

function exportarCSV() {

  const dados =
    prepararDadosRelatorio()

  if (!dados.length) {

    $q.notify({
      type: 'warning',
      message: 'Não existem bens para exportar.'
    })

    return false

  }

  try {

    const colunas =
      Object.keys(dados[0])

    const linhas =
      dados.map(item => {

        return colunas.map(coluna => {

          const valor =
            texto(item[coluna])
              .replace(/"/g, '""')

          return `"${valor}"`

        }).join(';')

      })

    const csv = [

      colunas.join(';'),

      ...linhas

    ].join('\n')


    const blob = new Blob(

      [
        '\ufeff' + csv
      ],

      {
        type:
          'text/csv;charset=utf-8;'
      }

    )


    baixarArquivo(

      blob,

      `relatorio-bens-${dataArquivo()}.csv`

    )


    $q.notify({

      type: 'positive',

      message:
        'CSV exportado com sucesso.'

    })


    return true

  } catch (erro) {

    console.error(
      'Erro ao exportar CSV:',
      erro
    )

    $q.notify({

      type: 'negative',

      message:
        'Não foi possível gerar o CSV.'

    })

    return false

  }

}


/* ======================================================
   EXPORTAÇÃO PRINCIPAL
====================================================== */

function exportarRelatorio() {

  if (exportando.value) {

    return

  }


  exportando.value =
    true


  let sucesso =
    false


  try {

    switch (
      formato.value
    ) {


      case 'pdf':

        sucesso =
          exportarPDF()

        break


      case 'excel':

        exportarExcel()

        break


      case 'csv':

        exportarCSV()

        break

    }


    /*
     * Fecha o diálogo somente
     * quando o PDF foi gerado.
     */

    if (sucesso) {

      dialogExportar.value =
        false

    }

  } finally {

    exportando.value =
      false

  }

}

</script>
```
