package fixers.jBugger.DatabaseEntitites;

import lombok.Data;
import fixers.jBugger.Validators.AttachmentSizeValidation;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(exclude = "attachedFile")
@Entity
public class Attachment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String attachmentName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @AttachmentSizeValidation
    private byte[] attachedFile;
}
