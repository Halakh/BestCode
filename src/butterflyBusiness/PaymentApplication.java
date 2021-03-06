package butterflyBusiness;

import butterflyBusiness.csv.CSVFileWriter;
import butterflyBusiness.files.FileReader;
import butterflyBusiness.intelligence.BusinessFileWriter;
import butterflyBusiness.operation.PaymentOperation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaymentApplication {

    public static void main(String[] args) {

        FileReader fileReader = new FileReader();

        List<String> lines = fileReader.asList("butterflyBusiness/buterfly-business.txt");

        List<String> parsedLines = IntStream.iterate(0, i -> i + 3).limit(lines.size() / 3)
                .mapToObj(i -> {
                    return translateStrangeDigits(lines, i);
                })
                .collect(Collectors.toList());

        List<PaymentOperation> paymentOperations = getPaymentOperations(parsedLines);

        paymentOperations.forEach(butterflyBusiness.operation.PaymentOperation::updateIssuerFromCreditNumber);

        paymentOperations.sort(Comparator.comparing(butterflyBusiness.operation.PaymentOperation::getDate));

        createDir("src/butterflyBusiness/output");

        CSVFileWriter.writeCSV(paymentOperations, "src/butterflyBusiness/output/Payments.csv");

        BusinessFileWriter.writeBusinessIntelligenceReport(paymentOperations, "src/butterflyBusiness/output/Report.txt");
    }

    private static void createDir(String filePath) {
        try {
            Path dir = Paths.get(filePath);
            if (!Files.exists(dir))
                Files.createDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String translateStrangeDigits(List<String> lines, int i) {
        List<String> partition0 = partition(lines.get(i), 3);
        List<String> partition1 = partition(lines.get(i + 1), 3);
        List<String> partition2 = partition(lines.get(i + 2), 3);

        List<String> strangeList = IntStream.range(0, partition0.size())
                .mapToObj(index -> partition0.get(index) + partition1.get(index) + partition2.get(index))
                .collect(Collectors.toList());

        return strangeList.stream()
                .map(digit -> butterflyBusiness.operation.SevenSegmentMapping.strangeDigits.get(digit))
                .collect(Collectors.joining());
    }

    private static List<PaymentOperation> getPaymentOperations(List<String> parts) {
        return parts.stream()
                .map(line -> line.split(" "))
                .map(columns -> new PaymentOperation(
                        LocalDate.parse(columns[0]),
                        columns[1],
                        Double.valueOf(columns[2])))
                .collect(Collectors.toList());
    }

    private static List<String> partition(String line, int size) {
        return IntStream.iterate(0, i -> i + size).limit(line.length() / size)
                .mapToObj(index -> line.substring(index, index + size))
                .collect(Collectors.toList());
    }


}
