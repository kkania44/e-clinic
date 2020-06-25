package pl.clinic.project.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import pl.clinic.project.model.AppointmentWithPatientData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePdf {

    public static ByteArrayInputStream appointmentsList(List<AppointmentWithPatientData> appointments, String date) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(75);
            table.setWidths(new int[]{1, 3, 4, 4});

            PdfPCell headCell;
            headCell = createHeadCell("Id.");
            table.addCell(headCell);
            headCell = createHeadCell("Godzina");
            table.addCell(headCell);
            headCell = createHeadCell("Imie Pacjenta");
            table.addCell(headCell);
            headCell = createHeadCell("Nazwisko Pacjenta");
            table.addCell(headCell);

            for (AppointmentWithPatientData appointment : appointments) {
                PdfPCell cell;

                cell = createOrdinaryCellWithString(appointment.getId().toString());
                table.addCell(cell);
                cell = createOrdinaryCellWithString(appointment.getTime().toString());
                table.addCell(cell);
                cell = createOrdinaryCellWithString(appointment.getPtFirstName());
                table.addCell(cell);
                cell = createOrdinaryCellWithString(appointment.getPtLastName());
                table.addCell(cell);
            }

            PdfWriter.getInstance(document, outputStream);
            document.open();

            Chunk chunkWithDate = new Chunk("Wizyty z dnia " +date, FontFactory.getFont(FontFactory.TIMES_BOLD));
            chunkWithDate.setTextRise(15F);
            document.add(chunkWithDate);

            document.add(table);
            document.addTitle(date);
            document.close();

        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdf.class.getName()).log(Level.SEVERE, "Error occured during generating PDF", ex);
        }

        return new ByteArrayInputStream(outputStream.toByteArray());

    }

    private static PdfPCell createHeadCell(String title) {
        Font headFont = FontFactory.getFont(FontFactory.TIMES_BOLD);
        PdfPCell headCell;
        headCell = new PdfPCell(new Phrase(title, headFont));
        headCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return headCell;
    }

    private static PdfPCell createOrdinaryCellWithString(String text) {
        Font cellFont = FontFactory.getFont(FontFactory.TIMES);
        PdfPCell cell;
        cell = new PdfPCell(new Phrase(text, cellFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

}
