package com.gestion.util;

import com.gestion.dto.FacturaDTO;
import com.gestion.dto.ProductoDTO;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class FacturaPDF {
    private Document document;
    private FacturaDTO facturaDTO;
    private Font smallBold = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL);
    private Font catFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
    private Font small = new Font(Font.TIMES_ROMAN, 10);
    private BaseFont bfBold, bf;

    public FacturaPDF() {}

    public ByteArrayInputStream createPdf(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter docWriter = null;
            initializeFonts();
            document = new Document(PageSize.A4);
            docWriter = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte cb = docWriter.getDirectContent();

            addMetaData(document);
            generateHeader(document, cb);
            addTitlePage(document);
            document.add(createTable());
            document.close();
            System.out.println("Documento cerrado");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public FacturaDTO getFactura() {
        return facturaDTO;
    }

    public void setFactura(FacturaDTO factura) {
        this.facturaDTO = factura;
    }

    //add the images
    private void addMetaData(Document document) {
        document.addTitle("Factura Nº: " + facturaDTO.getNumero());
        document.addSubject("Fecha:" + new SimpleDateFormat("yyyy").format(facturaDTO.getPedido().getFecha()));
        document.addKeywords("Facturas PDF");
        document.addAuthor("CASA VAZQUEZ");
        document.addCreator("CASA VAZQUEZ");
    }

    private  void addTitlePage(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        String fecha = new SimpleDateFormat("dd / MM / yyyy").format(facturaDTO.getPedido().getFecha());

//        Paragraph titulo = new Paragraph("FACTURA Nº " + factura.getNumero() +
//                                  "\nFecha: " + fecha, catFont);
//        titulo.setAlignment(Element.ALIGN_RIGHT);

//        preface.add(titulo);
        addEmptyLine(preface, 2);

        document.add(preface);

//        preface = new Paragraph("CENTRO VETERINARIO \"El Rey de la Casa\"");
        preface.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
        addEmptyLine(preface, 2);
        document.add(preface);

//        preface = new Paragraph("Cliente: " + factura.getCliente().getFullname() +
//                                "\nNIF/CIF: " + factura.getCliente().getNif() +
//                                "\n" + factura.getCliente().getDireccion() +
//                                "\n" + factura.getCliente().getCiudad().getPoblacion() +
//                                " (" + factura.getCliente().getProvincia().getProvincia() + ")" +
//                                "\nTeléfono: " + factura.getCliente().getTelefono(), smallBold);
//        preface.setAlignment(Element.ALIGN_LEFT);
        addEmptyLine(preface, 2);
        document.add(preface);
        //preface.add(new Paragraph("This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
        //    redFont));

        //document.add(preface);
        // Start a new page
        // document.newPage();
    }

    private  void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public PdfPTable createTable() throws DocumentException {
        // a table with three columns
        int iva = 0, iva2= 0;
        DecimalFormat df = new DecimalFormat("0.00");
        PdfPTable table = new PdfPTable(5);
        table.setTotalWidth(new float[]{ 50, 65, 150, 65, 70});
        table.setLockedWidth(true);

        // the cell object
        // we add a cell with colspan 3
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setColspan(1);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthLeft(0);
        cell.setBorder(0);
        cell.setBorderColorLeft(Color.WHITE);
        cell.setBorderColorBottom(Color.WHITE);
        cell.setPaddingBottom(5);
        table.addCell(cell);
        table.addCell(cell);
        table.addCell(cell);
        table.addCell(cell);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("CANT."));
        cell.setColspan(1);
        cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("FECHA"));
        cell.setColspan(1);
        cell.setHorizontalAlignment (com.lowagie.text.Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("CONCEPTO"));
        cell.setColspan(1);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("PRECIO"));
        cell.setColspan(1);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("IMPORTE"));
        cell.setColspan(1);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPaddingTop(5);
        cell.setPaddingBottom(5);
        table.addCell(cell);


        for(ProductoDTO producto : facturaDTO.getPedido().getProductos()) {


            cell = new PdfPCell(new Phrase(producto.getCantidad() + "", small));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(new SimpleDateFormat("dd-MM-yyyy").format(facturaDTO.getPedido().getFecha()), small));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(producto.getNombre() + " ", small));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(producto.getPrecio()+ " $", small));
            cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            cell.setPaddingBottom(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(producto.getPrecio()*producto.getCantidad() + " $", small));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(5);
            table.addCell(cell);
        }

        /* COSTE TOTAL */
        cell = new PdfPCell(new Phrase(" "));
        cell.setColspan(1);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthLeft(0);
        cell.setBorder(0);
        cell.setBorderColorLeft(Color.WHITE);
        cell.setBorderColorBottom(Color.WHITE);
        cell.setPaddingBottom(5);
        table.addCell(cell);
        table.addCell(cell);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("TOTAL"));
        cell.setColspan(1);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPaddingBottom(5);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(facturaDTO.getPedido().getPrecioTotal() +" $", smallBold));
        cell.setColspan(1);
        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
        cell.setPaddingBottom(5);
        table.addCell(cell);

        return table;
    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text, int tam){
        cb.beginText();
        cb.setFontAndSize(bfBold, tam);
        cb.setFontAndSize(bfBold, tam);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();
    }

    private void createCliente(PdfContentByte cb, float x, float y, String text, int tam){
        cb.beginText();
        cb.setFontAndSize(bf, tam);
        cb.setFontAndSize(bf, tam);
        cb.setTextMatrix(x,y);
        cb.showText(text.trim());
        cb.endText();
    }

    private void initializeFonts() throws DocumentException , IOException {
        try {
            bfBold = BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateHeader(Document doc, PdfContentByte cb) {

        try {

            createHeadings(cb, 150, 790, "\"CASA VAZQUEZ\"", 10);
            createHeadings(cb, 150, 745, "Av. Madero, 1149", 8);
            createHeadings(cb, 150, 730, "Del Viso (Buenos Aires)", 8);
            createHeadings(cb, 150, 715, "Teléfono: 02320 472248", 8);

            cb.setLineWidth(1f);

            // Invoice Header box layout
            cb.roundRectangle(460, 770, 100, 50, 5 ); //x, y, ancho, alto, round
            cb.roundRectangle(460, 705, 100, 50, 5 ); //x, y, ancho, alto, round
            cb.moveTo(460,797); // (x,y)
            cb.lineTo(560,797);
            cb.moveTo(460,732);
            cb.lineTo(560,732);
            cb.stroke();

            createHeadings(cb, 480, 804, "FACTURA", 12);
            createHeadings(cb, 502, 780, "" + facturaDTO.getNumero(), 14);
            createHeadings(cb, 488, 739, "FECHA", 12);
            createHeadings(cb, 470, 715, new SimpleDateFormat("dd / MM / yyyy").format(facturaDTO.getPedido().getFecha()), 14);

            cb.setLineWidth(1f);
            cb.roundRectangle(25, 565, 540, 96, 5 ); //x, y, ancho, alto, round
            cb.stroke();

            cb.moveTo(25,643); // (x,y)
            cb.lineTo(565,643);
            cb.stroke();

            createHeadings(cb, 265, 650, "CLIENTE" , 12);
            createCliente(cb, 35, 626, "Nombre:    " + facturaDTO.getCliente().getNombre(), 11);
            createCliente(cb, 35, 608, "Dirección:  " + facturaDTO.getCliente().getDireccion(), 11);
            createCliente(cb, 35, 590, "Email:     " + facturaDTO.getCliente().getEmail() , 11);
            createCliente(cb, 35, 572, "Teléfono:   " + facturaDTO.getCliente().getTelefono(), 11);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    }
