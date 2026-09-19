package com.wayfinder.minecraft.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wayfinder.WayfinderMod;
import com.wayfinder.civilization.persistence.CivilizationStatePersistenceMapper;
import com.wayfinder.civilization.persistence.PersistedCivilizationNode;
import com.wayfinder.civilization.persistence.PersistedCivilizationRelationship;
import com.wayfinder.civilization.persistence.PersistedLandmark;
import com.wayfinder.civilization.persistence.PersistedPosition;
import com.wayfinder.civilization.persistence.PersistedReason;
import com.wayfinder.civilization.persistence.PersistedReasonEvaluation;
import com.wayfinder.civilization.state.CivilizationState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.List;

public final class WayfinderCivilizationSavedData
        extends SavedData {

    /*
     * Relationships are an additive optional field. Existing Milestone 9
     * worlds remain schema-compatible, so no destructive migration is needed.
     */
    public static final int CURRENT_VERSION = 1;

    private static final Codec<PersistedPosition> POSITION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x")
                            .forGetter(PersistedPosition::x),
                    Codec.INT.fieldOf("y")
                            .forGetter(PersistedPosition::y),
                    Codec.INT.fieldOf("z")
                            .forGetter(PersistedPosition::z)
            ).apply(instance, PersistedPosition::new));

    private static final Codec<PersistedLandmark> LANDMARK_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id")
                            .forGetter(PersistedLandmark::id),
                    POSITION_CODEC.fieldOf("anchor")
                            .forGetter(PersistedLandmark::anchor),
                    Codec.STRING.fieldOf("type")
                            .forGetter(PersistedLandmark::type),
                    Codec.DOUBLE.fieldOf("salience")
                            .forGetter(PersistedLandmark::salience),
                    Codec.DOUBLE.fieldOf("prominence")
                            .forGetter(PersistedLandmark::prominence),
                    Codec.DOUBLE.fieldOf("isolation")
                            .forGetter(PersistedLandmark::isolation)
            ).apply(instance, PersistedLandmark::new));

    private static final Codec<PersistedReason> REASON_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("type")
                            .forGetter(PersistedReason::type),
                    Codec.DOUBLE.fieldOf("contribution")
                            .forGetter(PersistedReason::contribution),
                    Codec.STRING.fieldOf("explanation")
                            .forGetter(PersistedReason::explanation)
            ).apply(instance, PersistedReason::new));

    private static final Codec<PersistedReasonEvaluation>
            REASON_EVALUATION_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    REASON_CODEC.fieldOf("primary")
                            .forGetter(
                                    PersistedReasonEvaluation::primary
                            ),
                    REASON_CODEC.listOf()
                            .optionalFieldOf(
                                    "supporting",
                                    List.of()
                            )
                            .forGetter(
                                    PersistedReasonEvaluation::supporting
                            )
            ).apply(
                    instance,
                    PersistedReasonEvaluation::new
            ));

    private static final Codec<PersistedCivilizationNode>
            NODE_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id")
                            .forGetter(
                                    PersistedCivilizationNode::id
                            ),
                    Codec.STRING.fieldOf("purpose")
                            .forGetter(
                                    PersistedCivilizationNode::purpose
                            ),
                    POSITION_CODEC.fieldOf("position")
                            .forGetter(
                                    PersistedCivilizationNode::position
                            ),
                    LANDMARK_CODEC.optionalFieldOf("target")
                            .forGetter(
                                    PersistedCivilizationNode::geographicTarget
                            ),
                    Codec.STRING.optionalFieldOf("target_node_id")
                            .forGetter(
                                    PersistedCivilizationNode
                                            ::civilizationTargetNodeId
                            ),
                    Codec.DOUBLE.fieldOf("score")
                            .forGetter(
                                    PersistedCivilizationNode::score
                            ),
                    REASON_EVALUATION_CODEC.fieldOf("reasons")
                            .forGetter(
                                    PersistedCivilizationNode::reasons
                            ),
                    Codec.STRING.fieldOf(
                                    "source_proposal_key"
                            )
                            .forGetter(
                                    PersistedCivilizationNode
                                            ::sourceProposalKey
                            )
            ).apply(
                    instance,
                    PersistedCivilizationNode::new
            ));

    private static final Codec<PersistedCivilizationRelationship>
            RELATIONSHIP_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id")
                            .forGetter(
                                    PersistedCivilizationRelationship::id
                            ),
                    Codec.STRING.fieldOf("type")
                            .forGetter(
                                    PersistedCivilizationRelationship::type
                            ),
                    Codec.STRING.fieldOf("source_node_id")
                            .forGetter(
                                    PersistedCivilizationRelationship
                                            ::sourceNodeId
                            ),
                    Codec.STRING.fieldOf("target_node_id")
                            .forGetter(
                                    PersistedCivilizationRelationship
                                            ::targetNodeId
                            ),
                    Codec.DOUBLE.fieldOf("score")
                            .forGetter(
                                    PersistedCivilizationRelationship
                                            ::score
                            ),
                    REASON_EVALUATION_CODEC.fieldOf("reasons")
                            .forGetter(
                                    PersistedCivilizationRelationship
                                            ::reasons
                            ),
                    Codec.STRING.fieldOf(
                                    "source_proposal_key"
                            )
                            .forGetter(
                                    PersistedCivilizationRelationship
                                            ::sourceProposalKey
                            )
            ).apply(
                    instance,
                    PersistedCivilizationRelationship::new
            ));

    private static final Codec<WayfinderCivilizationSavedData>
            CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.optionalFieldOf(
                                    "version",
                                    CURRENT_VERSION
                            )
                            .forGetter(
                                    WayfinderCivilizationSavedData
                                            ::version
                            ),
                    NODE_CODEC.listOf()
                            .optionalFieldOf(
                                    "nodes",
                                    List.of()
                            )
                            .forGetter(
                                    WayfinderCivilizationSavedData
                                            ::nodes
                            ),
                    RELATIONSHIP_CODEC.listOf()
                            .optionalFieldOf(
                                    "relationships",
                                    List.of()
                            )
                            .forGetter(
                                    WayfinderCivilizationSavedData
                                            ::relationships
                            )
            ).apply(
                    instance,
                    WayfinderCivilizationSavedData::new
            ));

    public static final SavedDataType<
            WayfinderCivilizationSavedData
            > TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            WayfinderMod.MOD_ID,
                            "civilization"
                    ),
                    WayfinderCivilizationSavedData::new,
                    CODEC
            );

    private final int version;
    private List<PersistedCivilizationNode> nodes;
    private List<PersistedCivilizationRelationship>
            relationships;

    public WayfinderCivilizationSavedData() {
        this(
                CURRENT_VERSION,
                List.of(),
                List.of()
        );
    }

    private WayfinderCivilizationSavedData(
            int version,
            List<PersistedCivilizationNode> nodes,
            List<PersistedCivilizationRelationship>
                    relationships
    ) {
        if (version > CURRENT_VERSION) {
            throw new IllegalStateException(
                    "Wayfinder civilization data version "
                            + version
                            + " is newer than supported version "
                            + CURRENT_VERSION
            );
        }

        this.version = version;
        this.nodes = List.copyOf(nodes);
        this.relationships =
                List.copyOf(relationships);
    }

    public int version() {
        return version;
    }

    public List<PersistedCivilizationNode> nodes() {
        return nodes;
    }

    public List<PersistedCivilizationRelationship>
            relationships() {
        return relationships;
    }

    public CivilizationState toDomainState() {
        return CivilizationStatePersistenceMapper.toDomain(
                nodes,
                relationships
        );
    }

    public void replace(
            CivilizationState state
    ) {
        List<PersistedCivilizationNode> nextNodes =
                CivilizationStatePersistenceMapper.toPersisted(
                        state
                );

        List<PersistedCivilizationRelationship>
                nextRelationships =
                CivilizationStatePersistenceMapper
                        .toPersistedRelationships(
                                state
                        );

        if (nodes.equals(nextNodes)
                && relationships.equals(
                        nextRelationships
                )) {
            return;
        }

        nodes = List.copyOf(nextNodes);
        relationships =
                List.copyOf(nextRelationships);

        setDirty();
    }
}
