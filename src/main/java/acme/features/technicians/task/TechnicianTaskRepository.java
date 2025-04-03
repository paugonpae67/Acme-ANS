
package acme.features.technicians.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircrafts.InvolvedIn;
import acme.entities.aircrafts.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("select t from Task t where t.technician.id =:technicianId")
	Collection<Task> findTasksByTechnicianId(int technicianId);

	@Query("select t from Task t where t.id =:taskId ")
	Task findTaskById(int taskId);
	@Query("select i from InvolvedIn i where i.task.id = :id")
	Collection<InvolvedIn> findTaskInvolvedIn(int id);

	@Query("select t from Task t where t.ticker = :ticker")
	Task findTaskByTicker(String ticker);

}
