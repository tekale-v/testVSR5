package com.pg.dra.structure_copy.repository;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dra.structure_copy.interfaces.IStructureCopyPreProcessSteps;
import com.pg.dra.structure_copy.models.Part;

import matrix.db.Context;

public class StructureLoader {
    private Part part;
    private boolean loaded;
    private String error;

    private StructureLoader(Part part, boolean loaded, String error) {
        this.part = part;
        this.loaded = loaded;
        this.error = error;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Part getPart() {
        return part;
    }

    public String getError() {
        return error;
    }

    public static class Load {
        private Context context;

        public Load(Context context) {
            this.context = context;
        }

        public StructureLoader now(String objectOid) {
            try {
                Part part = buildStructure(objectOid);
                return new StructureLoader(part, true, null);
            } catch (FrameworkException e) {
                return new StructureLoader(null, false, e.getMessage());
            }
        }

        private Part buildStructure(String objectOid) throws FrameworkException {
            IStructureCopyPreProcessSteps steps = new StructureCopyPreProcessSteps();
            return steps.getBOMStructure(this.context, objectOid);
        }
    }
}
